## Remember Url Hashes


### What It Does
This plugin helps your players avoid re-downloading server resource packs when you already have them.  


### Why Is This Plugin Needed?
- Vanilla Minecraft already tries to do this, but **if the server doesn't have** SHA1 hash manually specified in server.properties, the client downloads the pack every time  
- The plugin **stores the previous hash** and other resource pack data (based on the url), so the server can send that hash to players  
- If your pack url stays the same between updates, you can easily run a command **to update the hash**  
- You can even **set a new pack URL** from console and automatically prepare its hash!  


### Commands

![Commands](images/commands.png)

The plugin keeps track of your server resource pack's Id, URL **and SHA-1 hash**:  
You can check that with `/urlhashesstate`:  

![Plugin's Data State](images/pluginStateNew.png)


#### Updating the hash or changing the URL
If you updated the pack's url, the plugin will update the hash automatically, because the URL changed.  
But if your link is always the same, you can easily run a command to **update the hash** whenever you want:  

- `/urlHashesUpdateHash` to **update the SHA-1 hash**  

Or even tell the plugin to completely change the link to a new version and update the hash:  

- `/urlHashesChangePackUrl` to **change the resource pack link** and update hashes  
Note: this command can only be executed from console by default for security reasons  

Both will tell you that it started to download the pack to determine it's hash:

![Hash Update Or Full Pack URL Change](images/packUpdateOrFullChangeCombined.png)

And everything will always be visible in your console too:

![Console On Hash Update Or URL Change](images/consoleHashUpdateNew.png)

You can tell the plugin to save the data between restarts:

- `/urlhashestogglesaving` to toggle **data saving**

![Toggling Saving Data](images/savingDataToggle.png)

So when the server restarts or reloads, it won't download the pack again:

![Loaded With Saved Data](images/pluginLoadedWSave.png)

Note, that with saving and the same link all the time, you'll need to run `/urlHashesUpdateHash` yourself. Again, only if your link stays the same between updates.

### Config Options

You can edit plugin's settings in the config (`plugins/config/ProvidePackHashes`)
The only exclusive option there at the moment is whether you allow players (with permissions) to change the pack URL, or just console (by default).


### Supported Environment
PaperMC servers (1.21.1+)  

### Alternative approach
You can have the same behaviour on your client, if you play on a server with this issue and you aren't an administrator.  
For this, use my Fabric client-side mod: [Remember Url Hashes](https://modrinth.com/mod/rememberurlhashes/)
