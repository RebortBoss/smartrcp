package cn.smartinvoke.smartrcp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import cn.smartinvoke.IServerObject;
import cn.smartinvoke.gui.ObjectPool;
import cn.smartinvoke.rcp.CPerspective;
import cn.smartinvoke.smartrcp.gui.control.GlobalServiceId;

/**
 * ȫ�ַ����࣬���ฺ�������ǰӦ�ó�����Ϣ�����ṩ�����˳���һЩʵ�÷���
 * @author pengzhen
 *
 */
public class CApplication implements IServerObject {
    private Map<String,Object> dataMap=new HashMap<String, Object>();
	public CApplication() {
	   	
	}
	/**
	 * ��ó���İ�װ·��
	 * @return
	 */
	public String getBaseFolder(){
		return CPerspective.getRuntimeSwfFolder();
	}
	/**
	 * �˳�����
	 */
	public void exit(){
		PlatformUI.getWorkbench().close();
	}
	public void restart(){
		PlatformUI.getWorkbench().restart();
	}
	public String getRuntimeSwfPath(){
		return CPerspective.getRuntimeSWFPath();
	}
	private String debugPath;
	public void setDebugPath(String path){
		this.debugPath=path;
	}
	public String getDebugPath(){
		return this.debugPath;
	}
	public void setData(String key,Object data){
	   if(key!=null){
		 this.dataMap.put(key, data);
	   }
	}
	public void removeData(String key){
		if(key!=null){
			this.dataMap.remove(key);
		}
	}
	public Object getData(String key){
		//Object ret=null;
		if(key!=null){
			return this.dataMap.get(key);
		}else{
			return null;
		}
		//return ret;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	   	File f=null;
	   	f.list();
	   	
	}
	public void dispose() {
	   
	}
	//---------------------���öԻ���
	public  boolean openConfirm(String title, String message) {
		Shell mainShell=(Shell)ObjectPool.INSTANCE.getObject(GlobalServiceId.Swt_Main_Win);
		return MessageDialog.openConfirm(mainShell, title, message);
	}
	public  void openError(String title, String message) {
		Shell mainShell=(Shell)ObjectPool.INSTANCE.getObject(GlobalServiceId.Swt_Main_Win);
		MessageDialog.openError(mainShell, title, message);
	}
	public  void openInformation(String title, String message) {
		Shell mainShell=(Shell)ObjectPool.INSTANCE.getObject(GlobalServiceId.Swt_Main_Win);
		MessageDialog.openInformation(mainShell, title, message);
	}
	public  boolean openQuestion(String title, String message) {
		Shell mainShell=(Shell)ObjectPool.INSTANCE.getObject(GlobalServiceId.Swt_Main_Win);
		return MessageDialog.openQuestion(mainShell, title, message);
	}
	public  void openWarning(String title, String message) {
		Shell mainShell=(Shell)ObjectPool.INSTANCE.getObject(GlobalServiceId.Swt_Main_Win);
		MessageDialog.openWarning(mainShell, title, message);
	}
}