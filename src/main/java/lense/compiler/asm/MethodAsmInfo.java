package lense.compiler.asm;

import lense.compiler.typesystem.Visibility;

public class MethodAsmInfo {

	private String name;
	private String desc;
	private String signature;
	private String[] exceptions;
	private Visibility visibility;
	private boolean isAbstract;
	private boolean isDefault;

	public MethodAsmInfo(String name, String desc, String signature, String[] exceptions, Visibility visibility, boolean isAbstract, boolean isDefault) {
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		this.visibility = visibility;
		this.isAbstract = isAbstract;
		this.isDefault = isDefault;
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

	
	public boolean isDefault() {
		return isDefault;
	}

	





}
