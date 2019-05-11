package sky_bai.sponge.baigppapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.storage.WorldProperties;

import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimContexts;
import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;

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
	public String gp(@Source Player source, @Nullable @Token String token) {
		if (token != null) {
			Claim a1 = GriefPrevention.getApi().getClaimManager(source.getWorld()).getClaimAt(source.getLocation());
			Map<String, ClaimFlag> a2 = new HashMap<String, ClaimFlag>();
			for (ClaimFlag b1 : ClaimFlag.values()) {
				a2.put(b1+"", b1);
			}
			if (token.contains("flag:")) {
				String b1 = token.replace("flag:", "").replaceFirst(":(.*)", "");
				String b2 =  token.replace("flag:"+b1+":", "");
				if (a2.containsKey(b1)) {
					if (b2 != null) {
						return a1.getPermissionValue(a2.get(b1), b2, a1.getContext()).name().toLowerCase();
					}
					return a1.getPermissionValue(a2.get(b1), "any", a1.getContext()).name().toLowerCase();
				}
			}
		}
		return "";
	}
}
