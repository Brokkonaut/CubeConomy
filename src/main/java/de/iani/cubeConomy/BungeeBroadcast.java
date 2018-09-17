package de.iani.cubeConomy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeBroadcast implements PluginMessageListener {
    private CubeConomy plugin;

    public BungeeBroadcast(CubeConomy plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = in.readUTF();
            if (subchannel.equals("CubeConomy")) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                int type = msgin.readShort();
                if (type == 1) {
                    // int version =
                    msgin.readShort();

                    long msb = msgin.readLong();
                    long lsb = msgin.readLong();
                    UUID playerUUID = new UUID(msb, lsb);
                    Player target = plugin.getServer().getPlayer(playerUUID);
                    if (target != null) {
                        String targetMessage = msgin.readUTF();
                        target.sendMessage(targetMessage);
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not parse plugin message", e);
        }
    }

    public void sendMessage(Player sender, UUID target, String message) {
        if (sender == null) {
            Iterator<? extends Player> it = plugin.getServer().getOnlinePlayers().iterator();
            if (!it.hasNext()) {
                return;// cannot send - no players online
            }
            sender = it.next();
        }
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            ByteArrayOutputStream b2 = new ByteArrayOutputStream();
            DataOutputStream out2 = new DataOutputStream(b2);

            out2.writeShort(1);// type
            out2.writeShort(1);// version
            // V1
            out2.writeLong(target.getMostSignificantBits());
            out2.writeLong(target.getLeastSignificantBits());
            out2.writeUTF(message);

            byte[] payload = b2.toByteArray();

            out.writeUTF("Forward");
            out.writeUTF("ONLINE");
            out.writeUTF("CubeConomy");
            out.writeShort(payload.length);
            out.write(payload);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not serialize plugin message", e);
        }

        sender.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
