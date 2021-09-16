package fr.acore.spigot.utils;

import com.google.gson.JsonObject;

import fr.acore.spigot.api.plugin.IPlugin;
import fr.acore.spigot.jedis.manager.RedisManager;
import fr.acore.spigot.utils.time.TimerBuilder;

public class ServersInfo {
	
	private String name;
	private StatutsType statuts;
	private String description;
	private int maxPlayer;
	private int onlinePlayer;
	
	private TimerBuilder refresh;
	public boolean needUpdate() { return refresh.isFinish();}
	
	public ServersInfo(String name, StatutsType status, String description, int maxPlayer, long refreshDelay) {
		this.name = name;
		this.statuts = status;
		this.description = description;
		this.maxPlayer = maxPlayer;
		this.onlinePlayer = 0;
		refresh = new TimerBuilder(refreshDelay);
	}
	
	public void update(IPlugin<?> plugin) {
		//JsonObject json = new JsonObject();
		//json.addProperty("serverTarget", name);
		//json.addProperty("serverName", plugin.getServerName());
		//plugin.getManager(RedisManager.class).sendPacket();
		refresh.setCurrent(System.currentTimeMillis());
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public StatutsType getStatuts() {
		return statuts;
	}
	
	public void setStatuts(StatutsType statuts) {
		this.statuts = statuts;
	}
	
	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setOnlinePlayer(int onlinePlayer) {
		this.onlinePlayer = onlinePlayer;
	}
	
	public int getOnlinePlayer() {
		return this.onlinePlayer;
	}

}