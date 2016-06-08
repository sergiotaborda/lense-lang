package lense.compiler.type;

import java.util.List;

public interface CallableMemberSignature<M extends CallableMember<M>> {

	
	public List<CallableMemberMember<M>> getParameters();

	public String getName();
}
