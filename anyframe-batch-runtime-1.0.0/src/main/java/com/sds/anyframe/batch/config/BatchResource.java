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

package com.sds.anyframe.batch.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;

import com.sds.anyframe.batch.util.AddableLong;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class BatchResource implements Resource {
	
	public static enum Mode {
		READ,
		WRITE,
		UPDATE,
		DELETE,
		UNKNOWN
	}
	
	public static enum Type {
		FILE,
		DB,
		UNKNOWN
	}
	
	private String id = null;
	
	private String url = null;
	
	private String urlOld = null;
	
	private Mode mode = null;
	
	private Type type = null;
	
	private final AddableLong countReference = new AddableLong(0l);
	
	private Object data;
	
	private Map<String, String> attributes = new HashMap<String, String>();
	
	private List<BatchResource> children = new ArrayList<BatchResource>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public void setAttribute(String key, String value) {
		this.attributes.put(key, value);
	}

	public AddableLong getCountReference() {
		return countReference;
	}

	public List<BatchResource> getChildResource() {
		return this.children;
	}
	
	public void addChildResource(BatchResource resource) {
		this.children.add(resource);
	}
	
	public boolean hasChildren() {
		return this.children != null && this.children.size() > 0;
	}

	public void setUrlOrg(String url_old) {
		this.urlOld = url_old;
	}

	public String getUrlOrg() {
		return urlOld;
	}
	
	public Object getData() {
		return this.data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "id:" + this.id + " URL:" + this.url;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(url);
	}

	public boolean exists() {
		File file = new File(this.url);
		
		return file.exists();
	}

	public boolean isReadable() {
		File file = new File(this.url);
		
		return file.canRead();
	}

	public boolean isOpen() {
		return false;
	}

	public URL getURL() throws IOException {
		File file = new File(this.url);
		return file.toURI().toURL();
	}

	public URI getURI() throws IOException {
		File file = new File(this.url);
		return file.toURI();
	}

	public File getFile() throws IOException {
		return new File(this.url);
	}

	public long lastModified() throws IOException {
		return 0;
	}

	public Resource createRelative(String relativePath) throws IOException {
		return null;
	}

	public String getFilename() {
		return null;
	}

	public String getDescription() {
		return "file [" + this.url + "]";
	}
}
