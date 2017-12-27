package lense.compiler.asm;

import lense.compiler.typesystem.Visibility;

public class MethodAsmInfo {

	private String name;
	private String desc;
	private String signature;
	private String[] exceptions;
	private Visibility visibility;
	private boolean isAbstract;

	public MethodAsmInfo(String name, String desc, String signature, String[] exceptions, Visibility visibility, boolean isAbstract) {
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		this.visibility = visibility;
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getSignature() {
		return signature;
	}

	public String[] getExceptions() {
		return exceptions;
	}

	
	public Visibility getVisibility() {
		return visibility;
	}

	public boolean isAbstract() {
		return isAbstract;
	}





}
