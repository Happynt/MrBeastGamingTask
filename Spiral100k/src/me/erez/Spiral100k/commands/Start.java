package me.erez.Spiral100k.commands;
import me.erez.Spiral100k.Main;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

/*
 * Introduction:
 * 
 * At first, the plugin gets the player's exact location and subtracts the y axis by 1 so the blocks
 * will deploy underneath the player.
 * I marked the center with a diamond block.
 * 
 * In order to execute a couple of tasks at once, I made variables and functions for each side
 * (right side, upwards, left side, downwards)
 *  
 * The first while loop collects locations ("spots") for each place the spiral changes direction.
 * In addition, it also collects the amount of blocks it would take to reach the edge.
 * The loop stops when 100k blocks are reached.
 * Visual example: https://gyazo.com/03fb2774788ed7c7196e858ef970807d
 * 
 * The 4 for loops generate Runnables for each side with the given spot and amount.
 * The runnables' jobs are to fill lines by a given location and an amount
 * for example: Left side, Location [X=1,Y=0,Z=1], 2 blocks.
 * 
 * Once all the runnables are prepared, the BukkitScheduler comes in play.
 * The for loop aligns the runnables so the blocks will be pasted in 
 * the desired asked spiral pattern, from the inside to the outside
 * 
 */

public class Start implements CommandExecutor {
    private Main plugin;
    
    //Plugin constructor
    public Start(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("start").setExecutor(this);
    }
    
    //The ints which are used to calculate the spots locations
    private int rightCounter = 1;
    private int upCounter = 1;
    private int leftCounter = 2;
    private int downCounter = 2;
    
    
    //The spots which are being inserted into the runnables
    private HashMap<Location, Integer> rightSpots = new HashMap<>();
    private HashMap<Location, Integer> upSpots = new HashMap<>();
    private HashMap<Location, Integer> leftSpots = new HashMap<>();
    private HashMap<Location, Integer> downSpots = new HashMap<>();
    
    
    //The runnables which execute the 100k blocks paste
    private ArrayList<Runnable> rightRunnables = new ArrayList<>();
    private ArrayList<Runnable> upRunnables = new ArrayList<>();
    private ArrayList<Runnable> leftRunnables = new ArrayList<>();
    private ArrayList<Runnable> downRunnables = new ArrayList<>();
    

    
    
    private Location center = null; //Where the player stands
	private int blocks = 0; //The blocks counter
    
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	Player player = (Player) sender;
    	center = player.getLocation();
    	center.subtract(0, 1, 0); /*I added this line
    	so the blocks will spawn under the
    	player and not on the player */
    	    	
    	center.getBlock().setType(Material.DIAMOND_BLOCK);
    	
    	
    	
    	//Finds all spots with edges	
    	while(blocks <= 100000) {
    	
    		rightSide();
			upSide();
			leftSide();
			downSide();
		
    	}
    	

    	//Prepares the runnables
    	for(Location loc : rightSpots.keySet()) {
    		rightRunnables.add(rightTaskGenerator(loc, rightSpots.get(loc)));
    	}
    	

    	
    	for(Location loc : upSpots.keySet()) {
    		upRunnables.add(upTaskGenerator(loc, upSpots.get(loc)));
    	}
    	
    	
    	for(Location loc : leftSpots.keySet()) {
    		leftRunnables.add(leftTaskGenerator(loc, leftSpots.get(loc)));
    	}
    	

    	
    	for(Location loc : downSpots.keySet()) {
    		downRunnables.add(downTaskGenerator(loc, downSpots.get(loc)));
    	}
    	
    	
    	
    	
    	BukkitScheduler sched = player.getServer().getScheduler();
    	int size = rightRunnables.size();
    	
    	//Runs all the runnables in the right order
    	for(int i = 0; i < size; i++) {
    		sched.runTask(plugin, rightRunnables.get(i));
    		sched.runTaskLater(plugin, upRunnables.get(i), 1L);
    		sched.runTaskLater(plugin, leftRunnables.get(i), 2L);
    		sched.runTaskLater(plugin, downRunnables.get(i), 3L);
    	}
    	
    	
    	
    	
    	
    	
        
        return true;
    }
    
    /*The logic behind the calculations: 
    https://gyazo.com/19041bbed43191d6675c3c70b6128537
    */
    public void rightSide() {
    	rightSpots.put(center, rightCounter);
    	center = center.clone().add(0, 0, rightCounter);
    	
    	blocks += rightCounter;
    	
    	rightCounter += 2;
    }
    
    public void upSide() {
    	upSpots.put(center, upCounter);
    	center = center.clone().add(upCounter, 0, 0);
    	
    	blocks+= upCounter;
    	
    	upCounter += 2;
    }
    
    public void leftSide() {
    	leftSpots.put(center, leftCounter);
    	center = center.clone().subtract(0, 0, leftCounter);
    	
    	blocks += leftCounter;
    	
    	leftCounter += 2;
    }
    
    public void downSide() {
    	downSpots.put(center, downCounter);
    	center = center.clone().subtract(downCounter, 0, 0);
    	
    	blocks += downCounter;
    	
    	downCounter += 2;
    }
    
    /* These runnables fill a line of blocks 1 by 1 with 
     * given directions (right/up/left/down), location and amount.
     */
    public Runnable rightTaskGenerator(Location loc, int amount) {
    	    	
    	return new Runnable() {
    		
    		@Override
    		public void run(){
    			
    			for(int i = 1; i <= amount; i++) {
    				
    				loc.add(0, 0, 1).getBlock().setType(Material.CYAN_CONCRETE);
    				
    				
    			}
    			
    		}
    		
    	};
		
    }
    
    public Runnable upTaskGenerator(Location loc, int amount) {
    	
    	return new Runnable() {
    		
    		@Override
    		public void run(){
    			
    			for(int i = 1; i <= amount; i++) {
    				
    				loc.add(1, 0, 0).getBlock().setType(Material.RED_CONCRETE);
    				
    			}
    			
    		}
    		
    	};
		
    }
    
    public Runnable leftTaskGenerator(Location loc, int amount) {
    	
    	return new Runnable() {
    		
    		@Override
    		public void run(){
    			
    			for(int i = 1; i <= amount; i++) {
    				
    				loc.subtract(0, 0, 1).getBlock().setType(Material.YELLOW_CONCRETE);
    				
    			}
    			
    		}
    		
    	};
		
    }
    
    public Runnable downTaskGenerator(Location loc, int amount) {
    	
    	return new Runnable() {
    		
    		@Override
    		public void run(){
    			
    			for(int i = 1; i <= amount; i++) {
    				
    				loc.subtract(1, 0, 0).getBlock().setType(Material.LIME_CONCRETE);
    				
    			}
    			
    		}
    		
    	};
		
    }

    
    

    

    
    

    
}