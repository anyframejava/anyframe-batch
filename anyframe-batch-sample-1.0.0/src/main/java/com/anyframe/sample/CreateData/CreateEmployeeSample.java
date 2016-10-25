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

package com.anyframe.sample.CreateData;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.anyframe.sample.CreateData.Vo.EmployeeVo;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class CreateEmployeeSample {
	
	static int i=0;
	
	public static void makeData(EmployeeVo employeeVo){
		
		employeeVo.setNo1(i);
		employeeVo.setNo2(new BigDecimal(i));
		employeeVo.setNo3(new BigDecimal(i));
		employeeVo.setDate1(new Date(i*10000));
		employeeVo.setTimestamp1(new Timestamp(i));
		
		employeeVo.setName("n_"+i);
		employeeVo.setAddress("a_"+i);
		employeeVo.setDescription("d_"+i);
		i++;
 		
	}
}
