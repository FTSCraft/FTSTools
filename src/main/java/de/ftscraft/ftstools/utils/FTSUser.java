package de.ftscraft.ftstools.utils;

import de.ftscraft.ftstools.FTSTools;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class FTSUser {

    UUID uuid;
    List<String> skills;
    private final String skillPath;
    private final String skillName_LargeBukket;
    private final String skillName_MagicBundle;

    private final File file;
    private YamlConfiguration configuration;

    public FTSUser(UUID uuid) {
        this.uuid = uuid;

        skillPath = FTSTools.getInstance().getConfig().getString("skillsPath");
        skillName_LargeBukket = FTSTools.getInstance().getConfig().getString("skillName_LargeBukket");
        skillName_MagicBundle = FTSTools.getInstance().getConfig().getString("skillName_MagicBundle");
        file = new File(skillPath, uuid.toString()+".yml");

        if(file != null){
            if(file.exists()){
                configuration = YamlConfiguration.loadConfiguration(file);
                if(configuration.contains("skills")){
                    this.skills = configuration.getStringList("skills");
                }
            }
        }
    }

    public List<String> getSkills() {
        return skills;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void update(){
        if(file != null){
            if(file.exists()){
                configuration = YamlConfiguration.loadConfiguration(file);
                if(configuration.contains("skills")){
                    this.skills = configuration.getStringList("skills");
                }
            }
        }
    }

    public boolean hasLargeBukketSkill(){
        if(getSkills()!=null){
            if(getSkills().contains(skillName_LargeBukket)){
                return true;
            }
        }
        return false;
    }

    public boolean hasMagicBundleSkill(){
        if(getSkills()!=null){
            if(getSkills().contains(skillName_MagicBundle)){
                return true;
            }
        }
        return false;
    }

//    public boolean hasRequiredRank(){
//        PermissionManager permissionManager = new PermissionManager(LuckPermsProvider.get());
//        String group = permissionManager.getPlayerGroup(uuid).getName();
//        return group.equalsIgnoreCase("raeuber");
//    }

    public String getRequiredSkillName() {
        return skillName_LargeBukket;
    }
}
