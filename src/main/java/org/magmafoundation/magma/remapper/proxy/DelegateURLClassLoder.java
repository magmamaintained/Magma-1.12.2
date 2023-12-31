/*
 * Magma Server
 * Copyright (C) 2019-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.remapper.proxy;

/**
 * DelegateURLClassLoder
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:46 am
 */

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.magmafoundation.magma.remapper.utils.RemappingUtils;

public class DelegateURLClassLoder extends URLClassLoader {

    public static final String desc = DelegateURLClassLoder.class.getName().replace('.', '/');
    private final PluginDescriptionFile description;
    private final Map<String, Class<?>> classeCache = new HashMap<>();
    private final List<Package> packageCache = new ArrayList<>();


    {
        PluginDescriptionFile description = null;
        Class curClass = this.getClass();
        ClassLoader classLoader = curClass.getClassLoader();
        while (true) {
            if (classLoader == null) {
                break;
            }
            if (classLoader instanceof PluginClassLoader) {
                description = ((PluginClassLoader) classLoader).getDescription();
                break;
            }
            classLoader = classLoader.getClass().getClassLoader();
        }
        this.description = description;
    }

    public DelegateURLClassLoder(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    public DelegateURLClassLoder(final URL[] urls) {
        super(urls);
    }

    public DelegateURLClassLoder(final URL[] urls, final ClassLoader parent, final URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (name.startsWith(RemappingUtils.nmsPrefix)) {
            String mapName = RemappingUtils.map(name.replace('.', '/')).replace('/', '.');
            return JavaPlugin.class.getClassLoader().loadClass(mapName);
        }
        Class<?> result = this.classeCache.get(name);
        if (result != null) {
            return result;
        }
        synchronized (name.intern()) {
            result = this.classeCache.get(name);
            if (result != null) {
                return result;
            }
            result = this.remappedFindClass(name);
            if (result == null) {
                try {
                    result = super.findClass(name);
                } catch (ClassNotFoundException e) {
                    result = ((LaunchClassLoader) MinecraftServer.getServerInstance().getClass().getClassLoader()).findClass(name);
                }
            }
            if (result == null) {
                throw new ClassNotFoundException(name);
            }
            this.cacheClass(name, result);
        }
        return result;
    }

    protected Class<?> remappedFindClass(String name) throws ClassNotFoundException {
        Class<?> result = null;
        try {
            final String path = name.replace('.', '/').concat(".class");
            final URL url = this.findResource(path);
            if (url == null) {
                return null;
            }
            final InputStream stream = url.openStream();
            if (stream == null) {
                return null;
            }
            byte[] bytecode = IOUtils.toByteArray(stream);
            bytecode = RemappingUtils.remapFindClass(description, name, bytecode);
            final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            final URL jarURL = jarURLConnection.getJarFileURL();
            final CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);

            fixPackage(name, jarURLConnection.getManifest(), url);

            result = this.defineClass(name, bytecode, 0, bytecode.length, codeSource);
            if (result != null) {
                this.resolveClass(result);
            }
        } catch (Throwable t) {
            throw new ClassNotFoundException("Failed to remap class " + name, t);
        }
        return result;
    }

    protected void cacheClass(final String name, final Class<?> clazz) {
        this.classeCache.put(name, clazz);
        if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz);
        }
    }

    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fixPackage(String name, Manifest manifest, URL url) {
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            String pkgName = name.substring(0, dot);
            Package pkg = getPackage(pkgName);
            if (pkg == null) {
                try {
                    if (manifest != null) {
                        pkg = definePackage(pkgName, manifest, url);
                    } else {
                        pkg = definePackage(pkgName, null, null, null, null, null, null, null);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (!packageCache.contains(pkg) && manifest != null) {
                Attributes attributes = manifest.getMainAttributes();
                if (attributes != null) {
                    try {
                        try {

                            //check if the Package class contains the field implTitle

                            try {
                                Field _e = Package.class.getDeclaredField("implTitle");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE), "implTitle");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION), "implVersion");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR), "implVendor");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.SPECIFICATION_TITLE), "specTitle");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.SPECIFICATION_VERSION), "specVersion");
                                ObfuscationReflectionHelper.setPrivateValue(Package.class, pkg, attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR), "specVendor");
                            } catch (NoSuchFieldException e) {

                                Field versionInfoField = Package.class.getDeclaredField("versionInfo");
                                versionInfoField.setAccessible(true);
                                Object versionInfo = versionInfoField.get(pkg);

                                if (versionInfo != null) {
                                    setPrivateField(versionInfo, "implTitle", attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE));
                                    setPrivateField(versionInfo, "implVersion", attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
                                    setPrivateField(versionInfo, "implVendor", attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR));
                                    setPrivateField(versionInfo, "specTitle", attributes.getValue(Attributes.Name.SPECIFICATION_TITLE));
                                    setPrivateField(versionInfo, "specVersion", attributes.getValue(Attributes.Name.SPECIFICATION_VERSION));
                                    setPrivateField(versionInfo, "specVendor", attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR));
                                }

                            }


                        } catch (Exception ignored) {
                        }
                    } finally {
                        packageCache.add(pkg);
                    }
                }
            }
        }
    }
}
