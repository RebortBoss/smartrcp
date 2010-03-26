package smartrcp.plugin;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import cn.smartinvoke.IServerObject;
import cn.smartinvoke.gui.FlashViewer;
import cn.smartinvoke.rcp.CLayoutBasicInfo;
import cn.smartinvoke.rcp.CPageLayout;
import cn.smartinvoke.rcp.CPerspective;
import cn.smartinvoke.smartrcp.core.Perspective;
import cn.smartinvoke.smartrcp.core.SmartRCPViewPart;
import cn.smartinvoke.smartrcp.gui.FlashViewPart;
import cn.smartinvoke.smartrcp.gui.SplashWindow;
import cn.smartinvoke.smartrcp.gui.module.CObservable;

/**
 * 视图管理器，主要是flex调用此类的方法，实现java与flex的同步 这里的视图包括：ViewPart ,Shell窗口
 * 
 * @author pengzhen
 * 
 */
public class ViewManager  extends CObservable implements IServerObject{
	private IWorkbenchWindow workbenchWindow;
	public ViewManager(IWorkbenchWindow workbenchWindow) {
		this.workbenchWindow=workbenchWindow;
	}
	/**
	 * 根据配置信息打开一viewPart
	 * 如果CLayoutBasicInfo 的viewId以.swf结尾，打开FlashViewPart视图，并加载对应的swf，
	 * 否则打开viewId在plugin.xml中定义的视图。
	 * 
	 * @param basicInfo
	 * @return
	 */
	public FlashViewer openViewPart(CLayoutBasicInfo basicInfo,boolean isMultiple, int state) {
		try {
			FlashViewer ret=null;int appId=-1;
			IWorkbenchPage page=this.workbenchWindow.getActivePage();
			if (basicInfo != null) {
				String viewId=basicInfo.getViewId();
				if(viewId!=null){
				  // IWorkbenchPage page=SmartRCPBuilder.window.getActivePage();
				   
				   if(viewId.endsWith(".swf")){//如果是swf
					  appId = FlashViewer.getViewNum();
					  basicInfo.autoLoad=true;//设置为true，以便FlashViewPart自动加载swf
					  Perspective.swfLayoutMap.put(Integer.valueOf(appId),basicInfo);
					  
					  FlashViewPart flashViewPart=(FlashViewPart)page.showView(FlashViewPart.ID, appId + "",state);
					  ret=flashViewPart.getFlashViewer();
				   }else{
					  Integer num=FlashViewer.getViewNum();
					  IViewPart showView=null;
					  if(isMultiple){
						 try{
						 showView=page.showView(viewId, num+"", state);
						 }catch(Exception e){e.printStackTrace();};
					  }
					  if(showView==null){
					    showView=page.showView(viewId);
					  }
					  if(showView!=null && (showView instanceof SmartRCPViewPart)){
						  SmartRCPViewPart smartRCPViewPart=(SmartRCPViewPart)showView;
						  ret=smartRCPViewPart.getFlashViewer();
					  }
				   }
				}
			}
			return ret;
		} catch (Throwable e) {
			if(basicInfo!=null){
			  throw new RuntimeException("view "+basicInfo.getViewId()+" create fault,check isMultiple param or viewId");
			}else{
			  throw new RuntimeException(e.getMessage());
			}
		}
	}
	public void showViewPart(String appId){
		if(appId!=null){
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		 FlashViewer flashViewer=this.findFlashViewer(appId);
		 if(flashViewer!=null){//如果appId对应的视图已经初始化
		  Object parent=flashViewer.getParent();
		  if(parent instanceof IViewPart){
			  IViewPart part=(IViewPart)parent;
			  page.activate(part);
		  }
		 }else{//还没初始化或不存在
		    IViewReference[] refs=page.getViewReferences();
		    if(refs!=null){
		    	String moduleStr=
		    		CPageLayout.Instance.getModuleStr(appId);
		    	if(moduleStr==null){
		    		throw new RuntimeException("appId为"+appId+"的视图不存在所以无法打开");
		    	}
		    	for(int n=0;n<refs.length;n++){
		    		IViewReference ref=refs[n];
		    		if(ref.getId().equals(FlashViewPart.ID)){//flash容器视图
		    			if(ref.getSecondaryId().equals(appId)){
		    				page.activate(ref.getPart(true));
		    				break;
		    			}
		    		}else{//java容器视图
		    			if(ref.getId().equals(moduleStr)){
		    				page.activate(ref.getPart(true));
		    				break;
		    			}
		    		}
		    	}
		    }
		 }
		}
	}
	/**
	 * 删除viewPart在FlashViewer集合以及模块对应表中的所有信息
	 * @param part
	 */
	private void deletePartInfo(IWorkbenchPart part){
		if(part==null || !(part instanceof IViewPart)){
			return;
		}
		//从FlashViewer集合中删除掉当前关闭的ViewPart
		List<FlashViewer> curViews=FlashViewer.getViewers();//当前的所有FlashViewer
		for(int n=0;n<curViews.size();n++){
			FlashViewer viewer=curViews.get(n);
			if(viewer.getParent().equals(part)){//
				//从PageLayout的appId模块对应表中删除该part的信息
				CPageLayout.Instance.removeViewPartInfo(viewer.getModulePath(), viewer.getAppId());
				
				curViews.remove(n);
				break;
			}
		}
	}
	
	/**
	 * 关闭加载了modulePath模块的所有视图
	 * @param modulePath
	 */
	public void closeViewParts(String modulePath){
		if(modulePath!=null){
		  List<FlashViewer> views=this.findFlashViewers(modulePath);
		  for(int n=0;n<views.size();n++){
			  FlashViewer viewer=views.get(n);
			  this.closeViewPart(viewer.getParent());
		  }
		}
	}
	
	/**
	 * 关闭指定appId的视图
	 * @param appId
	 */
	public void closeViewPart(String appId){
		if(appId!=null){
			 FlashViewer flashViewer=this.findFlashViewer(appId);
			 if(flashViewer!=null){
			  this.closeViewPart(flashViewer.getParent());
			 }
		}
	}
	private void closeViewPart(Object parent){
		
		if(parent!=null && parent instanceof ViewPart){
			IWorkbenchPage page=this.workbenchWindow.getActivePage();
			ViewPart viewPart=(ViewPart)parent;
			//删除该viewPart的所有信息
			deletePartInfo(viewPart);
			
			page.hideView(viewPart);
			viewPart.dispose();
		}
	}
	//--------------------FlashView的管理方法
	/**
	 * 将指定的viewpart的状态，是最大化，最小化，还是保持原样
	 */
	public void  setState(String viewPartId,int state){
		//FlashViewer ret=null;
		if(viewPartId!=null){
		  IWorkbenchPage page=this.workbenchWindow.getActivePage();
		  FlashViewer flashViewer=this.findFlashViewer(viewPartId);
		  if(flashViewer!=null){
			  Object parent=flashViewer.getParent();
			  if(parent!=null && parent instanceof ViewPart){
				    ViewPart viewPart=(ViewPart)parent;
					//IWorkbenchPage page=SmartRCPBuilder.window.getActivePage();
				    
					//首先获得焦点
					page.activate(viewPart);
					//设置state
					IWorkbenchPartReference partReference=page.getActivePartReference();
					if(page.getPartState(partReference)!=state){
					 page.setPartState(partReference,state);
					}
			  }
		  }
		}
		//return ret;
	}
	
	/**
	 * 获得当前获得焦点的viewPart
	 * @return
	 */
	public FlashViewer getActiveViewPart(){
		FlashViewer ret=null;
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		IWorkbenchPart part=page.getActivePart();
		
		if(part!=null){
		  if(part instanceof FlashViewPart){
			FlashViewPart flashViewPart=(FlashViewPart)part;
			ret=flashViewPart.getFlashViewer();
		  }else{
			//java的ViewPart
			List<FlashViewer> curViews=FlashViewer.getViewers();
			for(int n=0;n<curViews.size();n++){
				FlashViewer viewer=curViews.get(n);
				if(viewer.getParent().equals(part)){
					return viewer;
				}
			}
		  }
		}
		return ret;
	}
	public List<String> findAppIds(String modulePath){
		
		/**List<String> ret=new LinkedList<String>();
		//检查
		IViewReference[] refs=SmartRCPBuilder.window.getActivePage().getViewReferences();
		if(refs!=null){
			for(int n=0;n<refs.length;n++){
				IViewReference ref=refs[n];
				
			}
		}*/
		List<String> ret=null;
		if(modulePath!=null){
			if(modulePath.endsWith(".swf")){
			 modulePath=CPerspective.getRuntimeSwfFolder() + "/"+modulePath;
			}
			ret=CPageLayout.Instance.getModuleIdAppIdMap().get(modulePath);
		}
		return ret;
	}
	public List<FlashViewer> findFlashViewers(String modulePath){
		List<FlashViewer> ret=new LinkedList<FlashViewer>();
		if(modulePath!=null){
			if(modulePath.endsWith(".swf")){
			  modulePath=CPerspective.getRuntimeSwfFolder() + "/"+modulePath;
			}
			List<FlashViewer> curViews=FlashViewer.getViewers();//当前的所有FlashViewer
			for(int n=0;n<curViews.size();n++){
				FlashViewer viewer=curViews.get(n);
				if(viewer.getModulePath().equals(modulePath)){
					ret.add(viewer);
				}
			}
		}
		return ret;
	}
	
	public FlashViewer findFlashViewer(String appId){
		if(appId!=null){
			List<FlashViewer> curViews=FlashViewer.getViewers();//当前的所有FlashViewer
			for(int n=0;n<curViews.size();n++){
				FlashViewer viewer=curViews.get(n);
				if(viewer.getAppId().equals(appId)){
					return viewer;
				}
			}
		}
		return null;
	}
	private ViewPart findViewPart(String appId){
		ViewPart ret=null;
		FlashViewer flashViewer=this.findFlashViewer(appId);
		if(flashViewer!=null){
			Object parent=flashViewer.getParent();
			if(parent instanceof IViewPart){
			  	 ret=(ViewPart)parent;
			}
		}
		return ret;
	}
	public void resetViews(){
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		page.resetPerspective();
	}
	public void close(){
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		page.close();
	}
	public void activate(String appId){
		ViewPart viewPart=this.findViewPart(appId);
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		if(viewPart!=null){
			page.activate(viewPart);
		}
	}
	public void bringToTop(String appId){
		ViewPart viewPart=this.findViewPart(appId);
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		if(viewPart!=null){
			page.bringToTop(viewPart);
		}
	}
	public void hideView(String appId){
		ViewPart viewPart=this.findViewPart(appId);
		if(viewPart!=null){
			IWorkbenchPage page=this.workbenchWindow.getActivePage();
			page.hideView(viewPart);
		}
	}
	public boolean isPartVisible(String appId){
		ViewPart viewPart=this.findViewPart(appId);
		//if(viewPart!=null){
		IWorkbenchPage page=this.workbenchWindow.getActivePage();
		return page.isPartVisible(viewPart);
	}
	public static void main(String[] args) {
		
	}
	public void dispose() {
		super.dispose();
	}
}
