package com.yongyida.nauncebaike.nuancetools;

import java.util.List;

public class NuanceBean {
	//public String diagnostic_info;
	public String type;
	public List<Interpretations> interpretations;
	
	public class Interpretations{
		public Action action;
		public Concepts concepts;
		public String literal;
	}
	
	public class Action{
		public Intents intent;
	}
	
	public class Intents{
		public String value;
		public float confidence;
	}
	
	public class Concepts{
		public List<Baike> baike_content;
	}
	
	public class Baike{
		public String value;
		public String literal;
		
	}
	
}