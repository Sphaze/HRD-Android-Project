package com.example.prj1;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class CommandContext extends BaseActivity{

	private String commandText="";
	public Command myCommand=null;

	// used for special processing in CtxCommand to validate the button-select command
	private final Pattern BUT_SEL=Pattern.compile("(?<=\\s{0,10}button-select\\s{0,10})\\w+", Pattern.CASE_INSENSITIVE);
	// used for special processing in CtxCommand to complete the set dropdown XXX YY # command
	private final Pattern SET_DROPDN=Pattern.compile("(?<=\\s{0,10}set\\s{1,10}dropdown\\s{0,10})(\\w+)\\s+(\\w+)", Pattern.CASE_INSENSITIVE);

	/**
	 * 
		 ?<=arg -- arg is via zero-width positive look-behind
		 \\s -- a whitespace character
		 \\s{0,10}set -- whitespace occurs at least 0 but not more than 10 times, including the word "set"
		 \\s{1,10}dropdown -- whitespace occurs at least 1 but not more than 10 times, including the word "dropdown"
		 \\s{0,10} -- whitespace occurs at least 0 but not more than 10 times
		 \\w+ -- a word character occurs 1 or more times
		 \\s+ -- a whitespace character occurs 1 or more times
		
	
		 (\\w+) this is for filtering 1 or more words in the next group 'mx.group(1)', it is going to be "Mode"
		 \\s+(\\w+)once there are 1 or more whitespaces, it filters 1 or more words in the group after that 'mx.group(2)', 
		 it is going to be a dropdown frequency mode such as LSB
		 
		 http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 */
	
	CommandContext(final String messageTxt) {

		super(R.string.app_name);

		myCommand=null;
		commandText=messageTxt.trim();

		//Matchers are needed for context sensitive commands
		Matcher mx=BUT_SEL.matcher(commandText); 


		if(mx.find())
		{
			final String button = mx.group(); //matches the button types "Lock", "TX"
		}

		mx=SET_DROPDN.matcher(commandText);

		if(mx.find())
		{       	
			String arg1=mx.group(1); //this would be "Mode" when running "radio.setMode(Mode.FM);"

			String arg2=mx.group(2);//this would be a dropdown frequency type when running "radio.setMode(Mode.FM);"

			if(!freqModes.containsKey(arg1.toLowerCase())){
		
				Log.e("ERRORLOC", "Missing dropdown: "+arg1);

			}

			//this list gets the radio drop downs which were previously put in the Hashmap "freqModes"
			List<String> arg2vals=freqModes.get(arg1.toLowerCase()); //arg1.toLowerCase() is "mode"

			int idx=0;

			//searches through all the drop down types
			for(String sg:arg2vals)
			{
				//finds the proper index for each frequency mode (if it finds the mode, it breaks from this loop 
				//and increments the index to the proper value
				if(sg.toLowerCase().equals(arg2.toLowerCase()))
				{
					break; //break out of this loop
				}

				idx++;
			}              

			if(idx>=arg2vals.size())
			{
				throw new IllegalArgumentException();
			}
			commandText=commandText+" "+Integer.toString(idx);
		}


		final StringBuilder sb=new StringBuilder();
		sb.append("[").append(hrdContext).append("] ").append(commandText);
		myCommand=new Command(sb.toString());
	}


	public void send(final RspHandler handler) {

		myCommand.sendCommand(handler);
	}
}



