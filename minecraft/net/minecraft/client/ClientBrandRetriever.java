package net.minecraft.client;

import axyl.client.Axyl;
import axyl.client.modules.features.other.ClientSpoofer;

public class ClientBrandRetriever
{
    public static String getClientModName()
    {
    	if(Axyl.ins.modManager.getModuleByName("ClientSpoofer").isToggled()) {
	        if(ClientSpoofer.spooferMode.getValString().equals("Null")) {
	        	return null;
	        } else {
	        	return ClientSpoofer.spooferMode.getValString().toLowerCase();
	        }
        } else {
        	return "vanilla";
        }
    }
}
