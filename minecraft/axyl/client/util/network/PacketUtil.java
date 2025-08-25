package axyl.client.util.network;
  
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.play.INetHandlerPlayClient;
import axyl.client.util.Utility;
import net.minecraft.network.Packet;

public class PacketUtil extends Utility {

    public static void sendPacket(final Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }
    
    public static void sendPacketPlayer(final Packet packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }
    
    public static void sendPacketPlayerNoEvent(final Packet packet) {
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);
    }
    
    public static void sendPacketNoEvent(final Packet packet) {
    	mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
    
	public static void handlePacket(Packet packet) {
		INetHandlerPlayClient netHandler = (INetHandlerPlayClient) mc.getNetHandler();
        if(packet instanceof S00PacketKeepAlive) {
            netHandler.handleKeepAlive((S00PacketKeepAlive) packet);
        }
        if(packet instanceof  S01PacketJoinGame) {
            netHandler.handleJoinGame((S01PacketJoinGame) packet);
        }
        if(packet instanceof S02PacketChat) {
            netHandler.handleChat((S02PacketChat) packet);
        }
        if(packet instanceof S03PacketTimeUpdate) {
            netHandler.handleTimeUpdate((S03PacketTimeUpdate) packet);
        }
        if(packet instanceof S04PacketEntityEquipment) {
            netHandler.handleEntityEquipment((S04PacketEntityEquipment) packet);
        }
        if(packet instanceof S05PacketSpawnPosition) {
            netHandler.handleSpawnPosition((S05PacketSpawnPosition) packet);
        }
        if(packet instanceof S06PacketUpdateHealth) {
            netHandler.handleUpdateHealth((S06PacketUpdateHealth) packet);
        }
        if(packet instanceof S07PacketRespawn) {
            netHandler.handleRespawn((S07PacketRespawn) packet);
        }
        if(packet instanceof S08PacketPlayerPosLook) {
            netHandler.handlePlayerPosLook((S08PacketPlayerPosLook) packet);
        }
        if(packet instanceof S09PacketHeldItemChange) {
            netHandler.handleHeldItemChange((S09PacketHeldItemChange) packet);
        }
        if(packet instanceof S10PacketSpawnPainting) {
            netHandler.handleSpawnPainting((S10PacketSpawnPainting) packet);
        }
        if(packet instanceof S0APacketUseBed) {
            netHandler.handleUseBed((S0APacketUseBed) packet);
        }
        if(packet instanceof S0BPacketAnimation) {
            netHandler.handleAnimation((S0BPacketAnimation) packet);
        }
        if(packet instanceof S0CPacketSpawnPlayer) {
            netHandler.handleSpawnPlayer((S0CPacketSpawnPlayer) packet);
        }
        if(packet instanceof S0DPacketCollectItem) {
            netHandler.handleCollectItem((S0DPacketCollectItem) packet);
        }
        if(packet instanceof S0EPacketSpawnObject) {
            netHandler.handleSpawnObject((S0EPacketSpawnObject) packet);
        }
        if(packet instanceof S0FPacketSpawnMob) {
            netHandler.handleSpawnMob((S0FPacketSpawnMob) packet);
        }
        if(packet instanceof S11PacketSpawnExperienceOrb) {
            netHandler.handleSpawnExperienceOrb((S11PacketSpawnExperienceOrb) packet);
        }
        if(packet instanceof S12PacketEntityVelocity) {
            netHandler.handleEntityVelocity((S12PacketEntityVelocity) packet);
        }
        if(packet instanceof S13PacketDestroyEntities) {
            netHandler.handleDestroyEntities((S13PacketDestroyEntities) packet);
        }
        if(packet instanceof S14PacketEntity) {
            netHandler.handleEntityMovement((S14PacketEntity) packet);
        }
        if(packet instanceof S18PacketEntityTeleport) {
            netHandler.handleEntityTeleport((S18PacketEntityTeleport) packet);
        }
        if(packet instanceof S19PacketEntityStatus) {
            netHandler.handleEntityStatus((S19PacketEntityStatus) packet);
        }
        if(packet instanceof S19PacketEntityHeadLook) {
            netHandler.handleEntityHeadLook((S19PacketEntityHeadLook) packet);
        }
        if(packet instanceof S1BPacketEntityAttach) {
            netHandler.handleEntityAttach((S1BPacketEntityAttach) packet);
        }
        if(packet instanceof S1CPacketEntityMetadata) {
            netHandler.handleEntityMetadata((S1CPacketEntityMetadata) packet);
        }
        if(packet instanceof S1DPacketEntityEffect) {
            netHandler.handleEntityEffect((S1DPacketEntityEffect) packet);
        }
        if(packet instanceof S1EPacketRemoveEntityEffect) {
            netHandler.handleRemoveEntityEffect((S1EPacketRemoveEntityEffect) packet);
        }
        if(packet instanceof S1FPacketSetExperience) {
            netHandler.handleSetExperience((S1FPacketSetExperience) packet);
        }
        if(packet instanceof S20PacketEntityProperties) {
            netHandler.handleEntityProperties((S20PacketEntityProperties) packet);
        }
        if(packet instanceof S21PacketChunkData) {
            netHandler.handleChunkData((S21PacketChunkData) packet);
        }
        if(packet instanceof S22PacketMultiBlockChange) {
            netHandler.handleMultiBlockChange((S22PacketMultiBlockChange) packet);
        }
        if(packet instanceof S23PacketBlockChange) {
            netHandler.handleBlockChange((S23PacketBlockChange) packet);
        }
        if(packet instanceof S24PacketBlockAction) {
            netHandler.handleBlockAction((S24PacketBlockAction) packet);
        }
        if(packet instanceof S25PacketBlockBreakAnim) {
            netHandler.handleBlockBreakAnim((S25PacketBlockBreakAnim) packet);
        }
        if(packet instanceof S26PacketMapChunkBulk) {
            netHandler.handleMapChunkBulk((S26PacketMapChunkBulk) packet);
        }
        if(packet instanceof S27PacketExplosion) {
            netHandler.handleExplosion((S27PacketExplosion) packet);
        }
        if(packet instanceof S28PacketEffect) {
            netHandler.handleEffect((S28PacketEffect) packet);
        }
        if(packet instanceof S29PacketSoundEffect) {
            netHandler.handleSoundEffect((S29PacketSoundEffect) packet);
        }
        if(packet instanceof S2APacketParticles) {
            netHandler.handleParticles((S2APacketParticles) packet);
        }
        if(packet instanceof S2BPacketChangeGameState) {
            netHandler.handleChangeGameState((S2BPacketChangeGameState) packet);
        }
        if(packet instanceof S2CPacketSpawnGlobalEntity) {
            netHandler.handleSpawnGlobalEntity((S2CPacketSpawnGlobalEntity) packet);
        }
        if(packet instanceof S2DPacketOpenWindow) {
            netHandler.handleOpenWindow((S2DPacketOpenWindow) packet);
        }
        if(packet instanceof S2EPacketCloseWindow) {
            netHandler.handleCloseWindow((S2EPacketCloseWindow) packet);
        }
        if(packet instanceof S2FPacketSetSlot) {
            netHandler.handleSetSlot((S2FPacketSetSlot) packet);
        }
        if(packet instanceof S30PacketWindowItems) {
            netHandler.handleWindowItems((S30PacketWindowItems) packet);
        }
        if(packet instanceof S31PacketWindowProperty) {
            netHandler.handleWindowProperty((S31PacketWindowProperty) packet);
        }
        if(packet instanceof S32PacketConfirmTransaction) {
            netHandler.handleConfirmTransaction((S32PacketConfirmTransaction) packet);
        }
        if(packet instanceof S33PacketUpdateSign) {
            netHandler.handleUpdateSign((S33PacketUpdateSign) packet);
        }
        if(packet instanceof S34PacketMaps) {
            netHandler.handleMaps((S34PacketMaps) packet);
        }
        if(packet instanceof S35PacketUpdateTileEntity) {
            netHandler.handleUpdateTileEntity((S35PacketUpdateTileEntity) packet);
        }
        if(packet instanceof S36PacketSignEditorOpen) {
            netHandler.handleSignEditorOpen((S36PacketSignEditorOpen) packet);
        }
        if(packet instanceof S37PacketStatistics) {
            netHandler.handleStatistics((S37PacketStatistics) packet);
        }
        if(packet instanceof S38PacketPlayerListItem) {
            netHandler.handlePlayerListItem((S38PacketPlayerListItem) packet);
        }
        if(packet instanceof S39PacketPlayerAbilities) {
            netHandler.handlePlayerAbilities((S39PacketPlayerAbilities) packet);
        }
        if(packet instanceof S3APacketTabComplete) {
            netHandler.handleTabComplete((S3APacketTabComplete) packet);
        }
        if(packet instanceof S3BPacketScoreboardObjective) {
            netHandler.handleScoreboardObjective((S3BPacketScoreboardObjective) packet);
        }
        if(packet instanceof S3CPacketUpdateScore) {
            netHandler.handleUpdateScore((S3CPacketUpdateScore) packet);
        }
        if(packet instanceof S3DPacketDisplayScoreboard) {
            netHandler.handleDisplayScoreboard((S3DPacketDisplayScoreboard) packet);
        }
        if(packet instanceof S3EPacketTeams) {
            netHandler.handleTeams((S3EPacketTeams) packet);
        }
        if(packet instanceof S3FPacketCustomPayload) {
            netHandler.handleCustomPayload((S3FPacketCustomPayload) packet);
        }
        if(packet instanceof S40PacketDisconnect) {
            netHandler.handleDisconnect((S40PacketDisconnect) packet);
        }
        if(packet instanceof S41PacketServerDifficulty) {
            netHandler.handleServerDifficulty((S41PacketServerDifficulty) packet);
        }
        if(packet instanceof S42PacketCombatEvent) {
            netHandler.handleCombatEvent((S42PacketCombatEvent) packet);
        }
        if(packet instanceof S43PacketCamera) {
            netHandler.handleCamera((S43PacketCamera) packet);
        }
        if(packet instanceof S44PacketWorldBorder) {
            netHandler.handleWorldBorder((S44PacketWorldBorder) packet);
        }
        if(packet instanceof S45PacketTitle) {
            netHandler.handleTitle((S45PacketTitle) packet);
        }
        if(packet instanceof S46PacketSetCompressionLevel) {
            netHandler.handleSetCompressionLevel((S46PacketSetCompressionLevel) packet);
        }
        if(packet instanceof S47PacketPlayerListHeaderFooter) {
            netHandler.handlePlayerListHeaderFooter((S47PacketPlayerListHeaderFooter) packet);
        }
        if(packet instanceof S48PacketResourcePackSend) {
            netHandler.handleResourcePack((S48PacketResourcePackSend) packet);
        }
        if(packet instanceof S49PacketUpdateEntityNBT) {
            netHandler.handleEntityNBT((S49PacketUpdateEntityNBT) packet);
        }
    }
}
