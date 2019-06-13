package sky_bai.sponge.baigppapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;
import me.ryanhamshire.griefprevention.api.data.PlayerData;

@Plugin(id = "baigppapi", name = "BaiGPPAPI", dependencies = { @Dependency(id = "placeholderapi"), @Dependency(id = "griefprevention") })
public class BaiGPPAPI {
	public static final Logger logger = LoggerFactory.getLogger("BaiGPPAPI");

	@Listener
	public void onServerStart(GamePreInitializationEvent event) {
		logger.info("插件加载中....");
	}

	@Listener
	public void onStart(GameStartingServerEvent e) {
		this.registerPAPI();
		logger.info("插件加载完成");
	}

	private void registerPAPI() {
		Optional<PlaceholderService> s = Sponge.getServiceManager().provide(PlaceholderService.class);
		if (s.isPresent()) {
			s.get().loadAll(this, this).stream().map(builder -> builder.author("sky_bai").version("1.0")).forEach(builder -> {
				try {
					builder.buildAndRegister();
				} catch (Exception ex) {
				}
			});
		}
	}
	
	@Placeholder(id = "gp")
	public Object GP2(@Source Player source, @Nullable @Token String token) {
		GriefPreventionApi gpApi = GriefPrevention.getApi();
		Claim onClaim = gpApi.getClaimManager(source.getWorld()).getClaimAt(source.getLocation());
		Optional<PlayerData> playeData = gpApi.getGlobalPlayerData(source.getUniqueId());
		Map<String, Object> GPPAPI = new HashMap<String, Object>();
		GPPAPI.put("onClaimOwnerName", onClaim.getOwnerName());
		GPPAPI.put("AccruedClaimBlocks", playeData.get().getAccruedClaimBlocks());
		GPPAPI.put("BlocksAccruedPerHour", playeData.get().getBlocksAccruedPerHour());
		GPPAPI.put("BonusClaimBlocks", playeData.get().getBonusClaimBlocks());
		GPPAPI.put("ChestClaimExpiration", playeData.get().getChestClaimExpiration());
		GPPAPI.put("CreateClaimLimit", playeData.get().getCreateClaimLimit());
		GPPAPI.put("InitialClaimBlocks", playeData.get().getInitialClaimBlocks());
		GPPAPI.put("MaxAccruedClaimBlocks", playeData.get().getMaxAccruedClaimBlocks());
		GPPAPI.put("RemainingClaimBlocks", playeData.get().getRemainingClaimBlocks());
		GPPAPI.put("ClaimLimit", playeData.get().getClaims().size());
		if (token.equalsIgnoreCase("debug")) {
			return onClaim.getData().getName();
		}
		if (token.equalsIgnoreCase("onClaimName")) {
			Optional<Text> claimNameOptional = onClaim.getData().getName();
			if (claimNameOptional.isPresent()) {
				return TextSerializers.PLAIN.serialize(claimNameOptional.get());
			}
			return "null";
		}
		Map<String, ClaimFlag> a2 = new HashMap<String, ClaimFlag>();
		for (ClaimFlag b1 : ClaimFlag.values()) {
			a2.put(b1 + "", b1);
		}
		for (String string : GPPAPI.keySet()) {
			if (token.equalsIgnoreCase(string)) {
				return GPPAPI.get(string);
			}
		}
		if (token.contains("flag:")) {
			String b1 = token.replace("flag:", "").replaceFirst(":(.*)", "");
			String b2 = token.replace("flag:" + b1 + ":", "");
			if (a2.containsKey(b1)) {
				if (b2 != null) {
					return onClaim.getPermissionValue(a2.get(b1), b2, onClaim.getContext()).name().toLowerCase();
				}
				return onClaim.getPermissionValue(a2.get(b1), "any", onClaim.getContext()).name().toLowerCase();
			}
		}
		return "";
	}
}
