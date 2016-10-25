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
package com.sds.anyframe.batch.agent.security;

import com.sds.anyframe.batch.agent.properties.SecurityConfigurations;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class SecurityManager {
	SecurityConfigurations configurations = SecurityConfigurations.getConfigurations();
	
	public boolean signIn(String userId, String password) throws AuthenticationException {
		if(configurations.getUserId() == null || configurations.getPassword() == null)
			throw new AuthenticationException("Security Manager could not find the configured userid or password");
		
		if(!userId.equals(configurations.getUserId()))
			throw new AuthenticationException("Incorrect userid, please make sure your userid");
		
		if(!password.equals(configurations.getPassword()))
			throw new AuthenticationException("Incorrect password, please make sure your password");
		
		
		return true;
	}
}
