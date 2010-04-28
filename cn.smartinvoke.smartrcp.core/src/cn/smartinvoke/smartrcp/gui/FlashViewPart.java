package cn.smartinvoke.smartrcp.gui;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import cn.smartinvoke.FlashContainer;
import cn.smartinvoke.ILoadCompleteListener;
import cn.smartinvoke.IServerObject;
import cn.smartinvoke.gui.FlashViewer;
import cn.smartinvoke.rcp.CLayoutBasicInfo;
import cn.smartinvoke.rcp.CPerspective;
import cn.smartinvoke.smartrcp.CApplication;
import cn.smartinvoke.smartrcp.core.Perspective;
import cn.smartinvoke.smartrcp.core.SmartRCPViewPart;
import cn.smartinvoke.smartrcp.gui.control.ViewManager;
import cn.smartinvoke.smartrcp.gui.module.CPartEvent;
import cn.smartinvoke.smartrcp.util.UIHelpMethod;
import cn.smartinvoke.util.ImageManager;

public class FlashViewPart extends SmartRCPViewPart implements IServerObject,
		ISaveablePart2 {
	public static final String ID = "cn.smartinvoke.smartrcp.gui.FlashViewPart"; //$NON-NLS-1$
	private CLayoutBasicInfo layoutInfo;

	private FlashViewer flashViewer;
	public IWorkbenchWindow window;
	/**
	 * viewPart的工具栏下拉菜单 管理器对象
	 */
    private ViewPartActionBar partActionBar;
	@Override
	public void createPartControl(Composite parent) {
		try {
			this.window = this.getViewSite().getWorkbenchWindow();
			String secondId = this.getViewSite().getSecondaryId();
			Map<Integer, CLayoutBasicInfo> layoutMap = Perspective.swfLayoutMap;
			if(layoutInfo==null){//持久化状态中没有就从map中取
			  layoutInfo = layoutMap.get(Integer.valueOf(secondId));
			}else{
			  layoutMap.put(Integer.valueOf(secondId), layoutInfo);
			}
			//如果没有则返回
			if(layoutInfo==null){
				return;
			}
			String modulePath = layoutInfo.modulePath;
			if (modulePath != null) {
				//
				String runtimeSwf=CApplication.Instance.getRuntimeSwfPath();
				String moduleFullPath=UIHelpMethod.getFullPath(modulePath);
				if (layoutInfo.isModuleSwf) {
					String[] paths = new String[] {runtimeSwf,moduleFullPath};
					flashViewer = new FlashViewer(secondId, parent, paths);
				} else {
					flashViewer = new FlashViewer(secondId, parent,moduleFullPath);
				}
				//是否是debug连接器打开的ViewPart
				flashViewer.debugModule=layoutInfo.isDebugLayout;
				// 设置父亲控件
				this.flashViewer.setParent(this);
				// 设置布局信息
				this.setViewTitle(layoutInfo.title);
                //debug
				//this.setPartName(flashViewer.getAppId());
				// 设置图标
				if (layoutInfo.image != null) {
					ImageDescriptor imageDescriptor = ImageManager
							.getImageDescriptor(layoutInfo.image);
					if (imageDescriptor != null) {
						this.setTitleImage(imageDescriptor.createImage());
					}
				}
				
				//当前flex模块加载完毕后调用ViewManager方法，唤醒其上的flex监听器，使
				//其他flex模块也可以监听到该模块的打开
				FlashContainer flashContainer=flashViewer.getFlashContainer();
				flashContainer.addListener(new ILoadCompleteListener() {
					
					public void run(){
					  ViewManager.Instance.fireEvent(CPartEvent.Part_Opened, FlashViewPart.this);
					}
				});
				// 加载swf
				if (layoutInfo.autoLoad){
					flashViewer.loadFlash();
				}
			}
			//初始化工具栏下拉菜单管理器对象
			this.partActionBar=new ViewPartActionBar(this.getViewSite().getActionBars());
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		
	}
	/**
	 * 显示当前的ViewPart
	 * @param state
	 */
    public void showViewPart(int state){
    	try{
    	this.getViewSite().getPage().showView(FlashViewPart.ID,
    			this.getViewSite().getSecondaryId(), state);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	public FlashViewer getFlashViewer() {
		return flashViewer;
	}

	public ViewPartActionBar getPartActionBar() {
		return partActionBar;
	}
    
	public void setViewTitle(String title) {
		
		super.setPartName(title);
	}

	public String getViewTitle() {
		return this.getPartName();
	}

	@Override
	public void setFocus() {
		//设置当前获得焦点的FlashViewer
		FlashViewer.curFlashViewer=this.flashViewer;
        
	}
    
	public IToolBarManager getToolBarManager() {
		
		return this.getViewSite().getActionBars().getToolBarManager();
	}

	public IMenuManager getMenuManager() {
		return this.getViewSite().getActionBars().getMenuManager();
	}

	public void dispose() {
		// 删除透视图对象中的layout信息对象
		try {
			if (this.flashViewer != null){
				//从布局map中注销掉自己
				Perspective.swfLayoutMap.remove(Integer.valueOf(this.flashViewer
						.getAppId()));
				
				this.flashViewer.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//super.dispose();
	}
	public String getType(){
    	return "FlashViewPart";
    }
	public int promptToSaveOnClose() {
		if(this.flashViewer==null){
			return ISaveablePart2.YES;
		}
		if(!this.flashViewer.isLoaded){
			return ISaveablePart2.NO;
		}
		if(this.isDirty()&&this.isSaveAsAllowed()){
			  Object obj=
				  this.flashViewer.getFlexApp().call("promptTitleAndMessage", new Object[]{});
			  Object[] arr=null;
			  if(obj!=null || obj.getClass().isArray()){
				  arr=(Object[])obj;
			  }
			  if(arr==null || arr.length<2){
				  arr=new Object[]{"是否保存","视图数据已经修改，是否保存"};
			  }
			  boolean ret=
				  MessageDialog.openConfirm(this.getViewSite().getShell(), arr[0]+"", arr[1]+"");
			  if(ret){
				 return ISaveablePart2.YES;
			  }else{
				 return ISaveablePart2.NO;
			  }
		}
		if(this.isDirty()&&this.isSaveOnCloseNeeded()){
			return ISaveablePart2.YES;
		}
		return ISaveablePart2.NO;
	}

	public void doSave(IProgressMonitor monitor) {
		if(this.flashViewer==null){
			return ;
		}
		if(!this.flashViewer.isLoaded){
			return ;
		}
		try{
		this.flashViewer.getFlexApp().asyncCall("doSave",
				null);
	}catch(Exception e){e.printStackTrace();};
	}

	public void doSaveAs() {
		if(this.flashViewer==null){
			return;
		}
		if(!this.flashViewer.isLoaded){
			return ;
		}
		try{
		this.flashViewer.getFlexApp().asyncCall("doSaveAs", new Object[] {});
	}catch(Exception e){e.printStackTrace();};
	}

	public boolean isDirty() {
		if(this.flashViewer==null){
			return false;
		}
		if(!this.flashViewer.isLoaded){
			return false;
		}
		try{
		Object retObj = this.flashViewer.getFlexApp().call("isDirty",
				new Object[] {});
		if (retObj != null) {
			return (Boolean) retObj;
		} else {
			return false;
		}
	}catch(Exception e){e.printStackTrace();};
	return false;
	}

	public boolean isSaveAsAllowed() {
		if(this.flashViewer==null){
			return false;
		}
		if(!this.flashViewer.isLoaded){
			return false;
		}
		try{
		Object retObj = this.flashViewer.getFlexApp().call("isSaveAsAllowed",
				new Object[] {});
		if (retObj != null) {
			return (Boolean) retObj;
		} else {
			return false;
		}
	}catch(Exception e){e.printStackTrace();};
	return false;
	}

	public boolean isSaveOnCloseNeeded() {
		if(this.flashViewer==null){
			return false;
		}
		if(!this.flashViewer.isLoaded){
			return false;
		}
		try{
		Object retObj = this.flashViewer.getFlexApp().call(
				"isSaveOnCloseNeeded", new Object[] {});
		if (retObj != null) {
			return (Boolean) retObj;
		} else {
			return false;
		}
		}catch(Exception e){e.printStackTrace();};
		return false;
	}
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		//Log.println("FlashViewPart secondId="+this.getViewSite().getSecondaryId());
		CLayoutBasicInfo layoutInfo=new CLayoutBasicInfo();
		if(layoutInfo.init(memento)){//信息已经存储到memento中
			this.layoutInfo=layoutInfo;
			//this.isRestorePart=true;
		}
	}
	public void saveState(IMemento memento) {  
	   this.layoutInfo.save(memento);
	}
}