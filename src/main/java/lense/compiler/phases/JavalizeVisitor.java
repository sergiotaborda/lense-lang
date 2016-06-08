package lense.compiler.phases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.objectweb.asm.ClassReader;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;

public class JavalizeVisitor implements Visitor<AstNode>{

	private SemanticContext semanticContext;
	private Map<String, File> nativeTypes;

	public JavalizeVisitor(SemanticContext semanticContext, Map<String, File> nativeTypes) {
		this.semanticContext = semanticContext;
		this.nativeTypes = nativeTypes;
	}

	@Override
	public void startVisit() {	}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {	
		
		if (node instanceof lense.compiler.ast.ClassTypeNode){
			ClassTypeNode n = (ClassTypeNode)node;
			
			if (n.isNative()){
				if (!nativeTypes.containsKey(n.getName())){
					throw new lense.compiler.CompilationError(node, "Native implementation for type " + n.getName() + " is missing");
				}
				
				// verify correct contract
				File classFile = nativeTypes.get(n.getName());
				try {
					TypeDefinition nativeType = readNative(classFile);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			//	TypeDefinition type = n.getSemanticContext().resolveTypeForName(n.getName(), n.getGenericParametersCount()).get();
				
				
			} else if (n.getKind() == lense.compiler.type.Kind.Class ){
				if (nativeTypes.containsKey(n.getName())){
					throw new lense.compiler.CompilationError(node, "Found native implementation for type " + n.getName() + " but type is not marked as native. Did you intended to mark " + n.getName() + " as native ?");
				}
			}
			
			
			
		} else if (node instanceof ArithmeticNode){
			ArithmeticNode n = (ArithmeticNode)node;
			
			MethodInvocationNode m = new MethodInvocationNode(n.getLeft(), n.getOperation().equivalentMethod(), n.getRight());
			n.getParent().replace(n, m);
		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode m = (MethodInvocationNode)node;
			
			
			if (!((lense.compiler.ast.TypedNode)m.getAccess()).getTypeVariable().getGenericParameters().isEmpty() ){
				
				if ( m.getTypeVariable() instanceof FixedTypeVariable){
					return ;
				}
				AstNode parent = m.getParent();
				TypeDefinition typeDefinition = m.getTypeVariable().getTypeDefinition();
				if (typeDefinition.getName().equals("lense.core.lang.Void")){
					return;
				}
				CastNode cast = new CastNode(m, typeDefinition);
				parent.replace(node, cast);
			}
		}
	}

	private TypeDefinition readNative(File classFile) throws IOException {
		
		ByteCodeReader cp = new ByteCodeReader();
		ClassReader cr = new ClassReader(new FileInputStream(classFile));
		cr.accept(cp, 0);
		

		return cp.getType();
	}

}
