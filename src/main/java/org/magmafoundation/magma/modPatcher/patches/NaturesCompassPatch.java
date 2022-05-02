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

package org.magmafoundation.magma.modPatcher.patches;

import org.magmafoundation.magma.modPatcher.ModPatcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * NaturesCompassPatch
 *
 * @author Kwright02 dontmailme@gmail.com
 * @since 02/01/2021 - 09:59 AM
 */
@ModPatcher.ModPatcherInfo(name = "naturessompass")
public class NaturesCompassPatch extends ModPatcher {

    @Override
    public byte[] transform(String name, String transformedName, byte[] clazz) {
        if (clazz == null) return null;

        if ("com.chaosthedude.naturescompass.util.PlayerUtils".equals(transformedName)) {
            return patchClass(clazz);
        }

        return clazz;
    }

    private byte[] patchClass(byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        retry:
        for (MethodNode methodNode : classNode.methods) {
            if ("canTeleport".equals(methodNode.name)) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    if (insnNode instanceof MethodInsnNode) {
                        if ("com/chaosthedude/naturescompass/util/PlayerUtils".equals(((MethodInsnNode) insnNode).owner)) {
                            break retry;
                        }
                    }
                }

                InsnList insnList = new InsnList();
                insnList.add(new InsnNode(Opcodes.ICONST_1));
                insnList.add(new InsnNode(Opcodes.IRETURN));
                methodNode.instructions.clear();
                methodNode.instructions.insert(insnList);
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

}
