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

package com.sds.anyframe.batch.manager.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchPreferenceMultipleInputDialog extends Dialog
{

    protected static final String BATCH_FIELD_NAME = "BATCH_FIELD_NAME";
    protected static final int BATCH_TEXT = 1000;
    protected static final int BATCH_BROWSE = 1010;
//    protected static final int BATCH_VARIABLE = 1020;
    protected Composite panel;
    protected List fieldList;
    protected List controlList;
    protected List validators;
    protected Map valueMap;
    private String title;
    
    protected class BatchFieldSummary
    {

        int type;
        String name;
        String initialValue;
        boolean allowsEmpty;

        public BatchFieldSummary(int type, String name, String initialValue, boolean allowsEmpty)
        {
            this.type = type;
            this.name = name;
            this.initialValue = initialValue;
            this.allowsEmpty = allowsEmpty;
        }
    }

    protected class Validator
    {

        boolean validate()
        {
            return true;
        }

        protected Validator()
        {
        }
    }


    public BatchPreferenceMultipleInputDialog(Shell shell, String title)
    {
        super(shell);
        fieldList = new ArrayList();
        controlList = new ArrayList();
        validators = new ArrayList();
        valueMap = new HashMap();
        this.title = title;
        setShellStyle(getShellStyle() | 16);
    }

    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        if(title != null)
            shell.setText(title);
    }

    protected Control createButtonBar(Composite parent)
    {
        Control bar = super.createButtonBar(parent);
        validateFields();
        return bar;
    }

    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(1808));
        panel = new Composite(container, 0);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(768));
        for(Iterator i = fieldList.iterator(); i.hasNext();)
        {
            BatchFieldSummary field = (BatchFieldSummary)i.next();
            switch(field.type)
            {
            case BATCH_TEXT: // 'd'
                createTextField(field.name, field.initialValue, field.allowsEmpty);
                break;

            case BATCH_BROWSE: // 'e'
                createBrowseField(field.name, field.initialValue, field.allowsEmpty);
                break;

//            case 1020: // 'f'
//                createVariablesField(field.name, field.initialValue, field.allowsEmpty);
//                break;
            }
        }

        fieldList = null;
        Dialog.applyDialogFont(container);
        return container;
    }

    public void addBrowseField(String labelText, String initialValue, boolean allowsEmpty)
    {
        fieldList.add(new BatchFieldSummary(BATCH_BROWSE, labelText, initialValue, allowsEmpty));
    }

    public void addTextField(String labelText, String initialValue, boolean allowsEmpty)
    {
        fieldList.add(new BatchFieldSummary(BATCH_TEXT, labelText, initialValue, allowsEmpty));
    }

//    public void addVariablesField(String labelText, String initialValue, boolean allowsEmpty)
//    {
//        fieldList.add(new BatchFieldSummary(1020, labelText, initialValue, allowsEmpty));
//    }

    protected void createTextField(String labelText, String initialValue, boolean allowEmpty)
    {
        Label label = new Label(panel, 0);
        label.setText(labelText);
        label.setLayoutData(new GridData(32));
        final Text text = new Text(panel, 2052);
        text.setLayoutData(new GridData(768));
        text.setData(BATCH_FIELD_NAME, labelText);
        label.setSize(label.getSize().x, text.getSize().y);
        if(initialValue != null)
            text.setText(initialValue);
        if(!allowEmpty)
        {
            validators.add(new Validator() {

                public boolean validate()
                {
                    return !text.getText().equals("");
                }

            }
);
            text.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e)
                {
                    validateFields();
                }

            }
);
        }
        controlList.add(text);
    }

    protected void createBrowseField(String labelText, String initialValue, boolean allowEmpty)
    {
        Label label = new Label(panel, 0);
        label.setText(labelText);
        label.setLayoutData(new GridData(32));
        Composite comp = new Composite(panel, 0);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(768));
        final Text text = new Text(comp, 2052);
        GridData data = new GridData(768);
        data.widthHint = 200;
        text.setLayoutData(data);
        text.setData(BATCH_FIELD_NAME, labelText);
        label.setSize(label.getSize().x, text.getSize().y);
        if(initialValue != null)
            text.setText(initialValue);
        if(!allowEmpty)
        {
            validators.add(new Validator() {

                public boolean validate()
                {
                    return !text.getText().equals("");
                }

            }
);
            text.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e)
                {
                    validateFields();
                }

            }
);
        }
        controlList.add(text);
    }


    protected void okPressed()
    {
        for(Iterator i = controlList.iterator(); i.hasNext();)
        {
            Control control = (Control)i.next();
            if(control instanceof Text)
                valueMap.put(control.getData(BATCH_FIELD_NAME), ((Text)control).getText());
        }

        controlList = null;
        super.okPressed();
    }

    public int open()
    {
        applyDialogFont(panel);
        return super.open();
    }

    public Object getValue(String key)
    {
        return valueMap.get(key);
    }

    public String getStringValue(String key)
    {
        return (String)getValue(key);
    }

    public void validateFields()
    {
        for(Iterator i = validators.iterator(); i.hasNext();)
        {
            Validator validator = (Validator)i.next();
            if(!validator.validate())
            {
                getButton(0).setEnabled(false);
                return;
            }
        }

        getButton(0).setEnabled(true);
    }
}
