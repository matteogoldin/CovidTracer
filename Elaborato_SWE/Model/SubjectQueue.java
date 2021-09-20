package Model;
import java.util.HashSet;
import java.util.Iterator;

public class SubjectQueue {
	private HashSet<Subject> subList;

	public SubjectQueue() {
		subList=new HashSet<Subject>();
	}
	
	public void addSubject(Subject sub){
		if(!subList.contains(sub))
			subList.add(sub);
	}

	public HashSet<Subject> getSubList() {
		return subList;
	}
}
