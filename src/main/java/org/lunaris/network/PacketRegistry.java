package org.lunaris.network;

import org.lunaris.network.packet.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PacketRegistry {

    private final Map<Byte, Supplier<Packet>> constructors = new HashMap<>();

    public PacketRegistry() {
        registerPackets(
                Packet01Login::new,
                Packet02PlayStatus::new,
                Packet03EncryptionRequest::new,
                Packet04EncryptionResponse::new,
                Packet05Disconnect::new,
                Packet06ResourcePacksInfo::new,
                Packet07ResourcePackStack::new,
                Packet08ResourcePackResponse::new,
                Packet09Text::new,
                Packet0AWorldTime::new,
                Packet0BStartGame::new,
                Packet0CAddPlayer::new,
                Packet0DAddEntity::new,
                Packet0ERemoveEntity::new,
                Packet0FAddItem::new,
                //Packet10
                Packet11PickupItem::new,
                Packet12MoveEntity::new,
                Packet13MovePlayer::new,
                //Packet14
                Packet15UpdateBlock::new,
                //Packet16
                //Packet17
                Packet18LevelSoundEvent::new,
                Packet19LevelEvent::new,
                //Packet1A
                Packet1BEntityEvent::new,
                //Packet1C
                Packet1DUpdateAttributes::new,
                Packet1EInventoryTransaction::new,
                Packet1FMobEquipment::new,
                Packet20MobArmorEquipment::new,
                //Packet21
                //Packet22
                //Packet23
                Packet24PlayerAction::new,
                //Packet25
                //Packet26
                Packet27SetEntityData::new,
                Packet28SetEntityMotion::new,
                //Packet29
                //Packet2A
                Packet2BSetSpawnPosition::new,
                Packet2CAnimate::new,
                Packet2DRespawn::new,
                Packet2EContainerOpen::new,
                Packet2FContainerClose::new,
                Packet30PlayerHotbar::new,
                Packet31InventoryContent::new,
                Packet32InventorySlot::new,
                //Packet33
                //Packet34
                //Packet35
                //Packet36
                Packet37AdventureSettings::new,
                //Packet38
                //Packet39
                Packet3AFullChunkData::new,
                Packet3BSetCommandsEnabled::new,
                Packet3CSetDifficulty::new,
                //Packet3D
                Packet3ESetPlayerGameType::new,
                Packet3FPlayerList::new,
                //Packet40
                //Packet41
                //Packet42
                //Packet43
                //Packet44
                Packet45RequestChunkRadius::new,
                Packet46ChunkRadiusUpdate::new,
                //Packet47
                //Packet48
                //Packet49
                //Packet4A
                //Packet4B
                Packet4CAvailableCommands::new,
                Packet4DCommandRequest::new,
                //Packet4E
                //Packet4F
                //Packet50
                //Packet51
                Packet52ResourcePackDataInfo::new
                //???
        );
    }

    public Packet constructPacket(byte id) {
        Supplier<Packet> constructor = this.constructors.get(id);
        return constructor == null ? null : constructor.get();
    }

    private void registerPackets(Supplier<Packet>... constructors) {
        for (Supplier<Packet> constructor : constructors) {
            Packet packet = constructor.get();
            this.constructors.put(packet.getID(), constructor);
        }
    }

}
