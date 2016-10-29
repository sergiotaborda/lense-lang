/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ir.stack.AbstractInvokeInstruction;
import lense.compiler.ir.stack.ArithmeticOperate;
import lense.compiler.ir.stack.CastInstruction;
import lense.compiler.ir.stack.CompareToZeroAndJump;
import lense.compiler.ir.stack.ComparisonOperation;
import lense.compiler.ir.stack.ConvertTypes;
import lense.compiler.ir.stack.DiscardTopStackValue;
import lense.compiler.ir.stack.DuplicateStackPosition;
import lense.compiler.ir.stack.DuplicateStackPositionPair;
import lense.compiler.ir.stack.GoTo;
import lense.compiler.ir.stack.IfJumpTo;
import lense.compiler.ir.stack.InvokeConstructor;
import lense.compiler.ir.stack.InvokeDynamic;
import lense.compiler.ir.stack.InvokeInterface;
import lense.compiler.ir.stack.InvokeStatic;
import lense.compiler.ir.stack.InvokeVirtual;
import lense.compiler.ir.stack.IsInstanceOf;
import lense.compiler.ir.stack.Label;
import lense.compiler.ir.stack.LoadFromConstantPool;
import lense.compiler.ir.stack.LoadFromVariable;
import lense.compiler.ir.stack.PushNumberValue;
import lense.compiler.ir.stack.PushStringValue;
import lense.compiler.ir.stack.ReadFieldFromObject;
import lense.compiler.ir.stack.ReadStaticField;
import lense.compiler.ir.stack.ReturnInstruction;
import lense.compiler.ir.stack.StackInstruction;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.stack.StoreToVariable;
import lense.compiler.ir.stack.ThrowException;
import lense.compiler.ir.stack.WriteFieldToObject;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Visibility;
/**
 * 
 */
public class OutToJavaClass implements CompilerBackEnd, Opcodes {

	FileLocations out;

	/**
	 * Constructor.
	 * @param out
	 * @throws IOException 
	 */
	public OutToJavaClass(FileLocations out) {
		this.out = out; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void use(CompiledUnit unit) {

		// 1 Transform Lense constructs and AST to Java Compatible constructs and AST ( do not use java.ast)
		// 1.1 Convert constructors to static methods, except default constructor
		// 1.2 Convert extension calls to static method calls
		// 1.3 Convert numbers to primitives where possible
		// 1.4 Convert operations to method calls {if not primitive)
		// 1.5 Handle native calls
		// 1.6 Handle special literal constructors
		// 1.7 Erase Any as super class and use Object with Any interface (??)
		
		// 2 Compile Java AST to Stack instructions 
		
		// 3 Use ASM to write .class file
		writeClassFile(unit);

		patchNative(unit);

	}

	private void patchNative(CompiledUnit unit) {
		AstNode node = unit.getAstRootNode().getChildren().get(0);

		if (!(node instanceof ClassTypeNode)){
			return;
		}
		ClassTypeNode t = (ClassTypeNode)node;

		if (t.isNative()){
			File sourceFile = new File (out.getNativeFolder(), t.getPackageName().replace('.', File.separatorChar) + File.separatorChar +  t.getSimpleName() + ".java");
			File compiledFile = new File (out.getNativeFolder(), t.getPackageName().replace('.', File.separatorChar) + File.separatorChar +  t.getSimpleName() + ".class");
			
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null,null, null, sourceFile.getPath());
			
			File targetCompiledFile = new File (out.getTargetFolder(), t.getPackageName().replace('.', File.separatorChar) + File.separatorChar +  t.getSimpleName() + ".class");
			
			compiledFile.renameTo(targetCompiledFile);
		}
	}

	private void writeClassFile(CompiledUnit unit) {
		File target = out.getTargetFolder();
		File compiled = target;
		int constructorsCount = 0;
		if (target.isDirectory()){
			AstNode node = unit.getAstRootNode().getChildren().get(0);

			if (!(node instanceof ClassTypeNode)){
				return;
			}
			ClassTypeNode t = (ClassTypeNode)node;
			
			if (t.isNative()){
				return;
			}
			String path = toJavaQN(t.getName());
			int pos = path.lastIndexOf('/');
			String filename = path.substring(pos+1) + ".class";
			File folder;
			if (pos >=0){
				path = path.substring(0, pos);
				folder = new File(target, path );
			} else {
				folder = target;
			}

			folder.mkdirs();

			compiled = new File(folder, filename);
			try {
				compiled.createNewFile();

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS & ClassWriter.COMPUTE_FRAMES);

				FieldVisitor fv;
				MethodVisitor mv;

				String superType = t.getSuperType() == null ? "java/lang/Object": toJavaQN(t.getSuperType().getName());
				
				cw.visit(52, ACC_PUBLIC + ACC_SUPER, toJavaQN(t.getName()), null, superType, null);

				cw.visitSource(unit.getUnit().getName(), null);

				for (AstNode child : t.getBody().getChildren()){
					if (child instanceof FieldDeclarationNode){
						FieldDeclarationNode n = (FieldDeclarationNode)child;
						int flags = n.getImutabilityValue() == Imutability.Imutable ? ACC_FINAL : 0;
						flags &= ACC_PRIVATE;
						fv = cw.visitField(flags, n.getName(), toJavaTypeName(n.getTypeVariable().getTypeDefinition().getName()), toJavaTypeName(n.getTypeVariable().getTypeDefinition().getName())/*Signature*/, null);
						fv.visitEnd();
					} else if (child instanceof MethodDeclarationNode){
						MethodDeclarationNode n = (MethodDeclarationNode)child;
						int flags = visibilityFlag(n.getVisibility()); 
						
						if (n.isAbstract()){
							flags &= ACC_ABSTRACT;
						}
						if (n.isStatic()){
							flags &= ACC_STATIC;
						}
						
						StringBuilder builder = toJavaParameters(n.getParameters());
						
						mv = cw.visitMethod(flags, n.getName(), "(" + builder.toString() + ")" + toJavaTypeName(n.getReturnType().getName()), null, null);
						mv.visitCode();
																				
						Optional<StackInstructionList> opStack =  n.getStackInstructionsList();
						
						if (!opStack.isPresent()){
							throw new RuntimeException("Stack not found for method " + n.getName());
						}
						StackInstructionList stack = opStack.get();
						
						writeStack(mv, stack);

						mv.visitEnd();

					
					} else if (child instanceof ConstructorDeclarationNode){
						ConstructorDeclarationNode c = (ConstructorDeclarationNode)child;
						
						if (c.isPrimary()){
							// must provide java constructor and static constructor method
							mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(" + toJavaParameters(c.getParameters()) + ")V", null, null);
							mv.visitCode();
							mv.visitVarInsn(ALOAD, 0);
							mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
							
							int index =1;
							for (AstNode p : c.getParameters().getChildren()){
								FormalParameterNode f = (FormalParameterNode)p;
								mv.visitVarInsn(ALOAD, 0);
								mv.visitVarInsn(ALOAD, index++);
								mv.visitFieldInsn(PUTFIELD, toJavaQN(c.getReturnType().getName()), f.getName(), toJavaTypeName(f.getTypeNode().getName()));
							}
						
							mv.visitInsn(RETURN);
	
							mv.visitEnd();
							
							// static method
							mv = cw.visitMethod(visibilityFlag(c.getVisibility()) & ACC_STATIC, "_constructor", "(" + toJavaParameters(c.getParameters()) +  ")" + toJavaTypeName(c.getReturnType().getName()), null, null);
							mv.visitCode();

							mv.visitTypeInsn(NEW, toJavaQN(c.getReturnType().getName()));
							mv.visitInsn(DUP);
							
							index =0;
							for (AstNode p : c.getParameters().getChildren()){
								mv.visitVarInsn(ALOAD, index++);
							}

							mv.visitMethodInsn(INVOKESPECIAL, toJavaQN(c.getReturnType().getName()), "<init>", "(" + toJavaParameters(c.getParameters()) + ")V", false);
							mv.visitInsn(ARETURN);
	

							mv.visitEnd();
						} else {
							// static method
							String name = "_constructor";
							if (c.getName() != null){
								name += "$" + c.getName();
							} else {
								constructorsCount++;
								name += "$" + Integer.toString(constructorsCount);
							}
							mv = cw.visitMethod(visibilityFlag(c.getVisibility()) & ACC_STATIC, name, "(" + toJavaParameters(c.getParameters()) +  ")" + toJavaTypeName(c.getReturnType().getName()), null, null);
							mv.visitCode();

							if (!c.isNative()){
								Optional<StackInstructionList> opStack =  c.getStackInstructionsList();
								
								if (!opStack.isPresent()){
									throw new RuntimeException("Stack not found for constructor  " + c.getName());
								}
								StackInstructionList stack = opStack.get();
								
								writeStack(mv, stack);
							}
							
							
							mv.visitEnd();
						}
					} else {
						throw new RuntimeException("Not recognized " + child.getClass().getName());
					}
				}

				cw.visitEnd();

				FileOutputStream  out = new FileOutputStream (compiled);
				out.write(cw.toByteArray());
				out.close();
				
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}

	private void writeStack(MethodVisitor mv, StackInstructionList stack) {
		Map<Integer, org.objectweb.asm.Label> labels = new HashMap<>();
		
		for(StackInstruction si : stack){
		    if (si instanceof InvokeConstructor){
		    	InvokeConstructor ni = (InvokeConstructor)si;
		    	mv.visitTypeInsn(NEW, toJavaQN(ni.getOwnerType().getName()));
		    	mv.visitInsn(DUP);
		    	mv.visitMethodInsn(INVOKESPECIAL, toJavaQN(ni.getOwnerType().getName()), "<init>", "()V", false); // TODO signature
			} else if (si instanceof AbstractInvokeInstruction){
				AbstractInvokeInstruction ni = (AbstractInvokeInstruction)si; // TODO parameters types
				int flag =0;
				if (ni instanceof InvokeVirtual){
					flag = INVOKEVIRTUAL;
				} else if (ni instanceof InvokeDynamic){
					flag = INVOKEDYNAMIC;
				} else if (ni instanceof InvokeStatic){
					flag = INVOKESTATIC;
				}else if (ni instanceof InvokeInterface){
					flag = INVOKEINTERFACE;
				}
				mv.visitMethodInsn(flag, toJavaTypeName(ni.getOwnerType().getName()), ni.getName(), "(I)" + toJavaTypeName(ni.getType().getName()), 
						ni.getOwnerType().getKind() == LenseUnitKind.Interface // TODO chelc the owner of the method declaration, not the invocakation target
				);
			}else if (si instanceof ArithmeticOperate){
				
			}else if (si instanceof CastInstruction){
				
			}else if (si instanceof ConvertTypes){
				
			}else if (si instanceof DiscardTopStackValue){
				
			}else if (si instanceof DuplicateStackPosition){
				
			}else if (si instanceof DuplicateStackPositionPair){
				
			}else if (si instanceof IsInstanceOf){
				
			}else if (si instanceof LoadFromConstantPool){
				LoadFromConstantPool ni = (LoadFromConstantPool)si;
				// TODO check if type is an object.
				mv.visitLdcInsn(ni.getValue());
			}else if (si instanceof LoadFromVariable){
				LoadFromVariable ni = (LoadFromVariable)si;
				// TODO check if type is an object.
				mv.visitVarInsn(ALOAD, ni.getVariableIndex());
				
			}else if (si instanceof StoreToVariable){
				StoreToVariable ni = (StoreToVariable)si;
				// TODO check if type is an object.
				mv.visitVarInsn(ASTORE, ni.getVariableIndex());
			}else if (si instanceof PushNumberValue){
				
			}else if (si instanceof PushStringValue){
				
			}else if (si instanceof ReadFieldFromObject){
				ReadFieldFromObject ni =(ReadFieldFromObject)si;
				mv.visitFieldInsn(GETFIELD, toJavaTypeName(ni.getOwnerType().getName()), ni.getName(), toJavaTypeName(ni.getType().getName()));
			}else if (si instanceof ReadStaticField){
				ReadStaticField ni =(ReadStaticField)si;
				mv.visitFieldInsn(GETSTATIC, toJavaTypeName(ni.getOwnerType().getName()), ni.getName(), toJavaTypeName(ni.getType().getName()));
			}else if (si instanceof ReturnInstruction){
				mv.visitInsn(ARETURN);
			}else if (si instanceof ThrowException){
				
			}else if (si instanceof WriteFieldToObject){
				
			}else if (si instanceof IfJumpTo){
				IfJumpTo ni = (IfJumpTo)si;
				org.objectweb.asm.Label label = labels.get(ni.getTargetLabel());
				if (label == null){
					label = new org.objectweb.asm.Label();
					labels.put(ni.getTargetLabel(), label);
				}
				mv.visitJumpInsn( ni.getLogicValue() ? IFEQ : IFNE , label);
			}else if (si instanceof GoTo){
				GoTo ni = (GoTo)si;
				org.objectweb.asm.Label label = labels.get(ni.getTargetLabel());
				
				if (label == null){
					label = new org.objectweb.asm.Label();
					labels.put(ni.getTargetLabel(), label);
				}
				
				mv.visitJumpInsn(GOTO, label);
			}else if (si instanceof CompareToZeroAndJump){
				CompareToZeroAndJump ni = (CompareToZeroAndJump)si;
				org.objectweb.asm.Label label = labels.get(ni.getTargetLabel());
				
				if (label == null){
					label = new org.objectweb.asm.Label();
					labels.put(ni.getTargetLabel(), label);
				}
				
				mv.visitJumpInsn(resolveComparisonOpCode(ni.getOperation()), label);
			}else if (si instanceof Label){
				Label ni = (Label)si;
				org.objectweb.asm.Label label = labels.get(ni.getLabel());
				
				if (label == null){
					label = new org.objectweb.asm.Label();
					labels.put(ni.getLabel(), label);
				}

				mv.visitLabel(label);
			} else {
				throw new RuntimeException("Unrecognized Stack instruction" + si.getClass());
			}
		}
	}

	private int resolveComparisonOpCode(ComparisonOperation co){
		switch (co){
		case EQUAL:
			return IF_ICMPEQ;
		case GREATER_OR_EQUAL_TO:
			return IF_ICMPGE;
		case GREATER_THAN:
			return IF_ICMPGT;
		case LESS_OR_EQUAL_TO:
			return IF_ICMPLE;
		case LESS_THAN:
			return IF_ICMPLT;
		case NOT_EQUAL:
			return IF_ICMPNE;
			default:
				throw new RuntimeException("Comparison not recognized");
		}
	}
	private StringBuilder toJavaParameters(ParametersListNode n) {
		StringBuilder builder = new StringBuilder();
		for(AstNode p : n.getChildren()){
			builder.append(toJavaTypeName(((FormalParameterNode)p).getTypeNode().getName()));
			//builder.append(",");
		}
		if (builder.length() > 0){
			builder.deleteCharAt(builder.length() -1);
		}
		return builder;
	}

	private int visibilityFlag(Visibility visibility) {
		if (visibility == null){
			return ACC_PRIVATE; 
		}
		
		switch(visibility){
		case Private:
			return ACC_PRIVATE;
		case Protected:
			return ACC_PROTECTED;
		case Public:
			return ACC_PUBLIC;
		default:
			throw new RuntimeException("Not recognized visibility");
		}
	}

	private String toJavaTypeName(String name) {
		return "L" + toJavaQN(name) + ";";
	}

	private String toJavaQN(String name) {
		return name.replace('.', '/');
	}

}
