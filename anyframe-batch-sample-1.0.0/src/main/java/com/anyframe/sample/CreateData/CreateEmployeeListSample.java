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
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.anyframe.sample.CreateData.Vo.EmployeeListVo;
import com.anyframe.sample.CreateData.Vo.EmployeeVo;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class CreateEmployeeListSample {
	
	static int i=0;
	
	public static void makeData(EmployeeListVo employeeListVo){
		
		List list = new ArrayList();
		
		for (int k=0; k <5; k++){
			EmployeeVo employeeVo = new EmployeeVo();
			CreateEmployeeSample.makeData(employeeVo);
			list.add(employeeVo);
		}
		employeeListVo.setEmployeeVoList(list);
		
		employeeListVo.setBigdecimal(new BigDecimal(i));
		employeeListVo.setBiginteger(new BigInteger(String.valueOf(i)));
		employeeListVo.setDate(new Date(i*10000));
		employeeListVo.setDesc("desc"+i);
		EmployeeVo employeeVo = new EmployeeVo();
		CreateEmployeeSample.makeData(employeeVo);
		employeeListVo.setEmployeeVo(employeeVo);
		employeeListVo.setNumber(i);
		employeeListVo.setTimestamp(new Timestamp(i));
 		
 		i++;
	}
}
