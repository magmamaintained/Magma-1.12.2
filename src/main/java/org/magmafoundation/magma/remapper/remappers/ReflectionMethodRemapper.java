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

package org.magmafoundation.magma.remapper.remappers;

import com.google.common.collect.Maps;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.remapper.mappingsModel.MethodRedirectRule;
import org.magmafoundation.magma.remapper.proxy.DelegateURLClassLoder;
import org.magmafoundation.magma.remapper.proxy.ProxyClass;
import org.magmafoundation.magma.remapper.proxy.ProxyClassLoader;
import org.magmafoundation.magma.remapper.proxy.ProxyClassWriter;
import org.magmafoundation.magma.remapper.proxy.ProxyField;
import org.magmafoundation.magma.remapper.proxy.ProxyMethod;
import org.magmafoundation.magma.remapper.proxy.ProxyMethodHandles_Lookup;
import org.magmafoundation.magma.remapper.proxy.ProxyMethodType;
import org.magmafoundation.magma.remapper.proxy.ProxyYamlConfiguration;
import org.magmafoundation.magma.remapper.utils.ASMUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * ReflectionMethodRemapper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:56 am
 */
public class ReflectionMethodRemapper extends MethodRemapper {

    private static final Map<String, Map<String, Map<String, MethodRedirectRule>>> methodRedirectMapping = new HashMap<>();
    private static final Map<String, Class<?>> virtualMethods = Maps.newHashMap();

    static {
        registerMethodRemapper("java/lang/Class", "forName", Class.class, new Class[]{String.class}, ProxyClass.class, false);
        registerMethodRemapper("java/lang/Class", "forName", Class.class, new Class[]{String.class, Boolean.TYPE, ClassLoader.class}, ProxyClass.class, false);
        registerMethodRemapper("java/lang/Class", "getField", Field.class, new Class[]{String.class}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/Class", "getDeclaredField", Field.class, new Class[]{String.class}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/Class", "getMethod", Method.class, new Class[]{String.class, Class[].class}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/Class", "getDeclaredMethod", Method.class, new Class[]{String.class, Class[].class}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/Class", "getName", String.class, new Class[]{}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/Class", "getSimpleName", String.class, new Class[]{}, ProxyClass.class, true);
        registerMethodRemapper("java/lang/reflect/Method", "getName", String.class, new Class[]{}, ProxyMethod.class, true);
        registerMethodRemapper("java/lang/reflect/Field", "getName", String.class, new Class[]{}, ProxyField.class, true);
        registerMethodRemapper("java/lang/invoke/MethodType", "fromMethodDescriptorString", MethodType.class, new Class[]{String.class, ClassLoader.class}, ProxyMethodType.class, false);
        registerMethodRemapper("java/lang/invoke/MethodHandles$Lookup", "unreflect", MethodHandle.class, new Class[]{String.class, ClassLoader.class}, ProxyMethodHandles_Lookup.class, true);
        registerMethodRemapper("java/lang/invoke/MethodHandles$Lookup", "findSpecial", MethodHandle.class, new Class[]{Class.class, String.class, MethodType.class, Class.class}, ProxyMethodHandles_Lookup.class, true);
        registerMethodRemapper("java/lang/invoke/MethodHandles$Lookup", "findStatic", MethodHandle.class, new Class[]{Class.class, String.class, MethodType.class}, ProxyMethodHandles_Lookup.class, true);
        registerMethodRemapper("java/lang/invoke/MethodHandles$Lookup", "findVirtual", MethodHandle.class, new Class[]{Class.class, String.class, MethodType.class}, ProxyMethodHandles_Lookup.class, true);
        registerMethodRemapper("java/lang/invoke/MethodHandles$Lookup", "findVirtual", MethodHandle.class, new Class[]{Class.class, String.class, MethodType.class}, ProxyMethodHandles_Lookup.class, true);
        registerMethodRemapper("java/lang/ClassLoader", "loadClass", Class.class, new Class[]{String.class}, ProxyClassLoader.class, true);
        registerMethodRemapper("java/net/URLClassLoader", "loadClass", Class.class, new Class[]{String.class}, ProxyClassLoader.class, false);
        registerMethodRemapper("java/net/URLClassLoader", "<init>", void.class, new Class[]{URL[].class, ClassLoader.class, URLStreamHandlerFactory.class}, DelegateURLClassLoder.class, false);
        registerMethodRemapper("java/net/URLClassLoader", "<init>", void.class, new Class[]{URL[].class, ClassLoader.class}, DelegateURLClassLoder.class, false);
        registerMethodRemapper("java/net/URLClassLoader", "<init>", void.class, new Class[]{URL[].class}, DelegateURLClassLoder.class, false);
        registerMethodRemapper("org/bukkit/configuration/file/YamlConfiguration", "loadConfiguration", YamlConfiguration.class, new Class[]{InputStream.class}, ProxyYamlConfiguration.class, false);
    }

    public ReflectionMethodRemapper(MethodVisitor mv, Remapper remapper) {
        super(mv, remapper);
    }

    public ReflectionMethodRemapper(int api, MethodVisitor mv, Remapper remapper) {
        super(api, mv, remapper);
    }

    private static void registerMethodRemapper(String owner, String name, Class returnType, Class[] args, Class remapOwner, boolean isVirtualMethod) {
        Map<String, Map<String, MethodRedirectRule>> byName = methodRedirectMapping.computeIfAbsent(owner, k -> new HashMap<>());
        Map<String, MethodRedirectRule> byDesc = byName.computeIfAbsent(name, k -> new HashMap<>());
        String methodDescriptor = ASMUtils.toMethodDescriptor(returnType, args);
        byDesc.put(methodDescriptor, new MethodRedirectRule(owner, name, methodDescriptor, remapOwner.getName().replace('.', '/')));
        if (isVirtualMethod) {
            virtualMethods.put(owner + ";" + name, remapOwner);
        }
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == Opcodes.NEW && "java/net/URLClassLoader".equals(type)) {
            type = DelegateURLClassLoder.desc;
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (Opcodes.INVOKEVIRTUAL == opcode) {
            redirectVirtual(opcode, owner, name, desc, itf);
        } else if (Opcodes.INVOKESTATIC == opcode) {
            redirectStatic(opcode, owner, name, desc, itf);
        } else if (Opcodes.INVOKESPECIAL == opcode) {
            redirectSpecial(opcode, owner, name, desc, itf);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    private MethodRedirectRule findRule(int opcode, String owner, String name, String desc, boolean itf) {
        Map<String, Map<String, MethodRedirectRule>> byOwner = methodRedirectMapping.get(owner);
        if (byOwner == null) {
            return null;
        }
        Map<String, MethodRedirectRule> byName = byOwner.get(name);
        if (byName == null) {
            return null;
        }
        MethodRedirectRule rule = byName.get(desc);
        return rule;
    }

    private void redirectSpecial(int opcode, String owner, String name, String desc, boolean itf) {
        MethodRedirectRule rule = findRule(opcode, owner, name, desc, itf);
        if (rule != null) {
            owner = rule.getRemapOwner();
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void redirectVirtual(int opcode, String owner, String name, String desc, boolean itf) {
        if (desc.equals("()[B")) {
            if (name.equals("toByteArray")) {
                if (owner.equals("com/comphenix/net/sf/cglib/asm/$ClassWriter")) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, ProxyClassWriter.class.getName().replace('.', '/'), "remapClass", "([B)[B", false);
                    return;
                }
            }
        }
        MethodRedirectRule rule = findRule(opcode, owner, name, desc, itf);
        if (rule != null) {
            opcode = Opcodes.INVOKESTATIC;
            Type r = Type.getReturnType(desc);
            Type[] args = Type.getArgumentTypes(desc);
            Type[] newArgs = new Type[args.length + 1];
            if ("org/magmafoundation/magma/remapper/proxy/ProxyClassLoader".equals(rule.getRemapOwner()) && "loadClass".equals(name)) {
                newArgs[0] = Type.getObjectType("java/lang/ClassLoader");
            } else {
                newArgs[0] = Type.getObjectType(owner);
            }
            owner = rule.getRemapOwner();
            System.arraycopy(args, 0, newArgs, 1, args.length);
            desc = Type.getMethodDescriptor(r, newArgs);
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void redirectStatic(int opcode, String owner, String name, String desc, boolean itf) {
        MethodRedirectRule rule = findRule(opcode, owner, name, desc, itf);
        if (rule != null) {
            owner = rule.getRemapOwner();
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }


    public static Map<String, Class<?>> getVirtualMethods() {
        return virtualMethods;
    }
}
