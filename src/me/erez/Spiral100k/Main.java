package me.erez.Spiral100k;

import org.bukkit.plugin.java.JavaPlugin;
import me.erez.Spiral100k.commands.Start;

public class Main extends JavaPlugin{
	
	@Override
	public void onEnable() {
		
		new Start(this);
		
	}

}
