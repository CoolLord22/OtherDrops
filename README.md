OtherDrops
==========

OtherDrops is a plugin for the Minecraft Bukkit API that lets you completely
customize what blocks and dead mobs drop when they are destroyed. Apples from
leaves, no more broken glass, and much, much more.

Please see the project page for full details here: <http://dev.bukkit.org/server-mods/otherdrops/>

Please see the spigot page for more download information here: <https://www.spigotmc.org/resources/otherdrops-updated.51793/>

Building from source
--------------------

These instructions assume you have already forked and/or cloned the project and have on your computer.

Rename `version.properties.CHANGEME` to `version.properties` and adjust the version numbers appropriately (see inside the file for more details).

OtherDrops comes with most dependencies already stored in the repository (for simplicity) however
you need to download a Bukkit build and place into the `lib` folder - rename it to `bukkit.jar`

Then build using your IDE or:

    $ ant jar

Use `ant -p` to see a complete list of Ant tasks.

Style guide and overview of coding structure is here: <https://github.com/Zarius/Bukkit-OtherBlocks/wiki/Coding-guide>.
