import java.util.ArrayList;
import java.util.List;



public class GenerateSign {
	
	public static void main(String []args){
		final List<String> list = new ArrayList<String>();
		 //list = new ArrayList<String>();
		list.add("hi");
		list.add("Bye");
	    for(String string:list){
		System.out.println(string);
	    
	    }
	    list.remove(0);
	    for(String string:list){
			System.out.println(string);
		    
		    }
	}
	
}


