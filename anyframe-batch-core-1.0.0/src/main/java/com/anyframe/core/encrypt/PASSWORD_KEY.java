/*                                                                           
 * Copyright 2010-2012 Samsung SDS Co., Ltd.                                 
 *                                                                           
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.          
 * You may obtain a copy of the License at                                   
 *                                                                           
 *     http://www.apache.org/licenses/LICENSE-2.0                            
 *                                                                           
 * Unless required by applicable law or agreed to in writing, software       
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and       
 * limitations under the License.                                            
 *                                                                           
 */                                                                          

package com.anyframe.core.encrypt;


/**
 * Common Encryption Key class for the password within Framework Configuration
 * currently used for JDBC Property file setting.
 *  
 * Read Jasypt Encryption/Decryption Library for more information(http://www.jasypt.org/)
 *  
 * @author prever.kang
 *
 */
public class PASSWORD_KEY
{

    public PASSWORD_KEY()
    {
    }

    public static final String BTO_PASSWORD_KEY = "%BTO_*%(#)_PBEWithMD5AndDES#%";
    public static final String BATCH_PASSWORD_KEY = "%BATCH_!@#$_AGENT_PBEWithMD5AndDES#%";
    public static final String ONLINE_PASSWORD_KEY = "%ONLINE_^%#D%_PBEWithMD5AndDES#%";
    public static final String NORMAL_PASSWORD_KEY = "%NORMAL_@!~$#_PBEWithMD5AndDES#%";
}
