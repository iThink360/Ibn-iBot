/** Event listener for when JDA is ready. */

package main;

import java.time.Instant;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Ready extends ListenerAdapter
{
	@Override
	public void onReady(@Nonnull ReadyEvent event)
	{
		// Send ready message
		EmbedBuilder readyDM = new EmbedBuilder()
				.setTitle("✅ Ibn iBot is now online.")
				.setColor(Main.getDefaultEmbedColor())
				.setDescription("JDA successfully logged into Discord API; all processes are ready.")
				.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getAvatarUrl())
				.setTimestamp(Instant.now());
		Main.getOwner().openPrivateChannel().complete().sendMessageEmbeds(readyDM.build()).queue();
	}
}
