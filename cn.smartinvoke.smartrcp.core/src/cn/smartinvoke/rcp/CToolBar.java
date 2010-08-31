package cn.smartinvoke.rcp;

import java.util.LinkedList;
import java.util.List;

import cn.smartinvoke.smartrcp.util.JFaceConstant;

public class CToolBar {
	public static final int Image_Text_Horizontal=0;
	public static final int Image_Text_Vertical=1;
	public static final int Image=2;
    public int type=Image_Text_Horizontal;
    //public String toolBarId=null;
    private List<String> actionIds=null;
	public CToolBar() {
		this.actionIds=new LinkedList<String>();
		this.actionIds.add(JFaceConstant.Menu_Separator_Str);
		//this.toolBarId=toolBarId;
	}
	/*public boolean equals(Object obj){
		if(obj!=null && obj instanceof CToolBar){
			CToolBar another=(CToolBar)obj;
			if(this.toolBarId!=null){
				if(this.toolBarId.equals(another.toolBarId)){
					return true;
				}
			}
		}
		return false;
	}
	public int hashCode(){
		return toolBarId.hashCode();
	}*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	public List<String> getActionIds() {
		return actionIds;
	}
	public void setActionIds(List<String> actionIds) {
		List<String> temp=actionIds;
		if(temp!=null){
		  this.actionIds = new LinkedList<String>();
		  for (int i = 0; i < temp.size(); i++) {
			  String item=temp.get(i);
			  if(!this.actionIds.contains(item)){
				  this.actionIds.add(item);
			  }
		  }
		}
	}
}
