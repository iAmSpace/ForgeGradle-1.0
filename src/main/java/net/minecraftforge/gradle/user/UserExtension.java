package net.minecraftforge.gradle.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraftforge.gradle.common.BaseExtension;
import net.minecraftforge.gradle.delayed.DelayedObject;

import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;

public class UserExtension extends BaseExtension
{
    // groups:  mcVersion  forgeVersion
    //private static final Pattern VERSION_CHECK = Pattern.compile("(?:[\\w\\d.-]+):(?:[\\w\\d-]+):([\\d.]+)-([\\d.]+)-(?:[\\w\\d.]+)");
    private static final Pattern VERSION_CHECK = Pattern.compile("([\\d.]+)-([\\w\\d.]+)(?:-[\\w\\d.]+)?");
    
    private String apiVersion;
    private ArrayList<Object> ats = new ArrayList<Object>();
    private HashMap<String, String> replacements = new HashMap<String, String>();
    private ArrayList<String> includes = new ArrayList<String>();
    
    public UserExtension(Project project)
    {
        super(project);
    }
    
    public void accessT(Object obj) { at(obj); }
    public void accessTs(Object... obj) { ats(obj); }
    public void accessTransformer(Object obj) { at(obj); }
    public void accessTransformers(Object... obj) { ats(obj); }

    public void at(Object obj)
    {
        ats.add(obj);
    }

    public void ats(Object... obj)
    {
        for (Object object : obj)
            ats.add(new DelayedObject(object, project));
    }

    public List<Object> getAccessTransformers()
    {
        return ats;
    }
    
    public void replace(Object token, Object replacement)
    {
        replacements.put(token.toString(), replacement.toString());
    }
    
    public void replace(Map<Object, Object> map)
    {
        for (Entry<Object, Object> e : map.entrySet())
        {
            replace(e.getKey(), e.getValue());
        }
    }
    
    public Map<String, String> getReplacements()
    {
        return replacements;
    }
    
    public List<String> getIncludes()
    {
        return includes;
    }
    
    public void replaceIn(String path)
    {
        includes.add(path);
    }
    
    public void setVersion(String str)
    {
        Matcher matcher = VERSION_CHECK.matcher(str);
        
        if (!matcher.matches())
            throw new IllegalArgumentException(str + " is not in the form 'MCVersion-apiVersion-branch'!");
        
        version = matcher.group(1);
        apiVersion = matcher.group(0);
        
        // to stop some derps with ForgeGradle versions
        if (!version.startsWith("1.6"))
            throw new IllegalArgumentException("This version of ForgeGradle only works with Minecraft 1.6.4!");
    }

    public String getApiVersion()
    {
        if (apiVersion == null)
            throw new ProjectConfigurationException("You must set the Minecraft Version!", new NullPointerException());
        
        return apiVersion;
    }
}
