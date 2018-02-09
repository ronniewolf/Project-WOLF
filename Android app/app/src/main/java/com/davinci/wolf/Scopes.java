package com.davinci.wolf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by aakash on 11/13/17.
 */
public class Scopes {
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface WolfApplicationScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface ConsoleActivityScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface LoginActivityScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface OptionsFragmentScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface ModeActivityScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface AutoActivityScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface ManualActivityScope {}
	
	@Scope @Retention(RetentionPolicy.RUNTIME)
	public @interface AboutActivityScope {}
}