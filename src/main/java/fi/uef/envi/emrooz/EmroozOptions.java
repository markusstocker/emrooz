/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

/**
 * <p>
 * Title: EmroozOptions
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Emrooz
 * </p>
 * <p>
 * Copyright: Copyright (C) 2015
 * </p>
 * 
 * @author Markus Stocker
 */

public class EmroozOptions {

	public final static String HOST = "localhost";
	
	public final static String KEYSPACE = "emrooz";
	
	public final static String DATA_TABLE = "data";
	
	public final static String REGISTRATIONS_TABLE = "registrations";
	
	public final static String ROWKEY_DATETIME_PATTERN = "yyyyMMddHHmmss";
	
	public final static String DATA_TABLE_ATTRIBUTE_1 = "key";
	
	public final static String DATA_TABLE_ATTRIBUTE_2 = "column";
	
	public final static String DATA_TABLE_ATTRIBUTE_3 = "value";
	
	public final static String REGISTRATIONS_TABLE_ATTRIBUTE_1 = "id";
	
	public final static String REGISTRATIONS_TABLE_ATTRIBUTE_2 = "sensor";
	
	public final static String REGISTRATIONS_TABLE_ATTRIBUTE_3 = "property";
	
	public final static String REGISTRATIONS_TABLE_ATTRIBUTE_4 = "feature";
	
	public final static String REGISTRATIONS_TABLE_ATTRIBUTE_5 = "rollover";
}
