package lense.compiler.typesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourcePath;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class TestTypeSystem {

	 //TODO test Array<T> specified to Array<boolean> returns method duplicate also with Array<Boolean>
	
	@Test
	public void testIsMethodSpecified() {
		LenseTypeDefinition array = new LenseTypeDefinition(
				"Array",
				LenseUnitKind.Class, 
				(LenseTypeDefinition) LenseTypeSystem.Any(), 
				Arrays.asList((TypeVariable)new RangeTypeVariable("T", Variance.Invariant, LenseTypeSystem.Nothing(), LenseTypeSystem.Any()))
		);
		
		LenseTypeDefinition arrayReturn = new LenseTypeDefinition(
				"Array",
				LenseUnitKind.Class, 
				(LenseTypeDefinition) LenseTypeSystem.Any(), 
				Arrays.asList((TypeVariable)new DeclaringTypeBoundedTypeVariable(array, 0, "T", Variance.Invariant))
		);
		
		array.addMethod("duplicate", arrayReturn);
		
		LenseTypeDefinition booleanType = new LenseTypeDefinition("Boolean",LenseUnitKind.Class, (LenseTypeDefinition) LenseTypeSystem.Any());
		
		LenseTypeDefinition booleanArray = LenseTypeSystem.specify(array, booleanType);
		
		Method method = booleanArray.getMethodsByName("duplicate").stream().findFirst().get();
		
		assertEquals( booleanType ,  method.getReturningType().getTypeDefinition().getGenericParameters().get(0));
	}
	
	@Test
	public void testIsRangeTypeVariablePromotable() {
		
		TypeVariable range = new RangeTypeVariable("T", Variance.Invariant,  LenseTypeSystem.Any(),LenseTypeSystem.Nothing());
		TypeVariable declare = new DeclaringTypeBoundedTypeVariable(LenseTypeSystem.Sequence(), 0, "T", Variance.Invariant);
		
		
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(range, declare).matches() );
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(declare, range).matches() );
	}
	
	@Test
	public void testIsGenericBoundTypeVariablePromotable() {
		
		LenseTypeDefinition array = new LenseTypeDefinition(
				"Array",
				LenseUnitKind.Class, 
				(LenseTypeDefinition) LenseTypeSystem.Any(), 
				Arrays.asList((TypeVariable)new RangeTypeVariable("T", Variance.Invariant, LenseTypeSystem.Nothing(), LenseTypeSystem.Any()))
		);
		
		TypeVariable generic = new GenericTypeBoundToDeclaringTypeVariable(array, array, 0, "T", Variance.Invariant);
		
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(array, generic).matches() );
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(generic, array).matches() );
	}
	
	@Test
	public void testIsArrayPromotableToArray() {
		
		LenseTypeDefinition array = new LenseTypeDefinition(
				"Array",
				LenseUnitKind.Class, 
				(LenseTypeDefinition) LenseTypeSystem.Any(), 
				Arrays.asList((TypeVariable)new RangeTypeVariable("T", Variance.Invariant, LenseTypeSystem.Nothing(), LenseTypeSystem.Any()))
		);
		
		LenseTypeDefinition otherArray = new LenseTypeDefinition(
				"Array",
				LenseUnitKind.Class, 
				(LenseTypeDefinition) LenseTypeSystem.Any(), 
				Arrays.asList((TypeVariable) new DeclaringTypeBoundedTypeVariable(array, 0, "T", Variance.Invariant))
		);
		
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(array, otherArray).matches() );
		assertTrue(LenseTypeSystem.getInstance().isAssignableTo(otherArray, array).matches() );
		
	}
	
	@Test
	public void testReadingAnyFromNativeFile() throws IOException {
		var basefolder = DiskSourceFileSystem.instance().folder(new File(".").getAbsoluteFile().getParentFile());
		
		ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(new UpdatableTypeRepository() {
			
			@Override
			public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
				return Optional.empty();
			}
			
			@Override
			public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
				return Collections.emptyMap();
			}
			
			@Override
			public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
				return type;
			}
		});
		
		var nativeTypeFile =  basefolder.file(SourcePath.of("lense","sdk","compilation","java","target","lense","core","lang", "Any.lense")); 

		TypeDefinition typeDef = reader.readNative(nativeTypeFile);
		
		assertNotNull(typeDef);
		
		assertNotNull(typeDef.getMembers());
		
		assertFalse(typeDef.getMembers().isEmpty());
	}
}
