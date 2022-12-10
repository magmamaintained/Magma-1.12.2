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
 * FastBenchPatch
 *
 * @author UeberallGebannt/LeStegii
 * @since 21/11/2022
 */
@ModPatcher.ModPatcherInfo(name = "FastWorkBench")
public class FastBenchPatch extends ModPatcher {

    @Override
    public byte[] transform(String name, String transformedName, byte[] clazz) {
        if (clazz == null) return null;

        if ("shadows.fastbench.FastBench".equals(transformedName)) {
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
            if ("serverStartRemoval".equals(methodNode.name)) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    if (insnNode instanceof MethodInsnNode) {
                        if ("shadows/fastbench/FastBench".equals(((MethodInsnNode) insnNode).owner)) {
                            break retry;
                        }
                    }
                }

                InsnList insnList = new InsnList();
                insnList.add(new InsnNode(Opcodes.RETURN));
                methodNode.instructions.clear();
                methodNode.instructions.insert(insnList);
            }
            if ("normalRemoval".equals(methodNode.name)) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    if (insnNode instanceof MethodInsnNode) {
                        if ("shadows/fastbench/FastBench".equals(((MethodInsnNode) insnNode).owner)) {
                            break retry;
                        }
                    }
                }

                InsnList insnList = new InsnList();
                insnList.add(new InsnNode(Opcodes.RETURN));
                methodNode.instructions.clear();
                methodNode.instructions.insert(insnList);
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
