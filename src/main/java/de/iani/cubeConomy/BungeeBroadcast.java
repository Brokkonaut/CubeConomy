package de.iani.cubeConomy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeBroadcast implements PluginMessageListener {
    private static final short MESSAGE_TYPE = 1;
    private static final short LEGACY_PAYLOAD_VERSION = 1;
    private static final short COMPONENT_JSON_PAYLOAD_VERSION = 2;
    private static final JSONComponentSerializer COMPONENT_SERIALIZER = JSONComponentSerializer.json();

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
                if (type == MESSAGE_TYPE) {
                    int version = msgin.readShort();

                    long msb = msgin.readLong();
                    long lsb = msgin.readLong();
                    UUID playerUUID = new UUID(msb, lsb);
                    Player target = plugin.getServer().getPlayer(playerUUID);
                    if (target != null) {
                        Component targetMessage;
                        if (version == LEGACY_PAYLOAD_VERSION) {
                            targetMessage = LegacyComponentSerializer.legacySection().deserialize(msgin.readUTF());
                        } else if (version == COMPONENT_JSON_PAYLOAD_VERSION) {
                            targetMessage = COMPONENT_SERIALIZER.deserialize(msgin.readUTF());
                        } else {
                            return;
                        }
                        target.sendMessage(targetMessage);
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not parse plugin message", e);
        }
    }

    public void sendMessage(Player sender, UUID target, Component message) {
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

            out2.writeShort(MESSAGE_TYPE);
            out2.writeShort(COMPONENT_JSON_PAYLOAD_VERSION);
            out2.writeLong(target.getMostSignificantBits());
            out2.writeLong(target.getLeastSignificantBits());
            out2.writeUTF(COMPONENT_SERIALIZER.serialize(message));

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
