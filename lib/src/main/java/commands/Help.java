/** Event listener for bot help. */
package commands;

import java.time.Instant;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter
{
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		if (event.getMessage().getContentRaw().equalsIgnoreCase(Main.getPrefix() + "help"))
		{
			EmbedBuilder help = new EmbedBuilder()
					.setColor(Main.getDefaultEmbedColor())
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.setTitle("❓ " + event.getJDA().getSelfUser().getName() + " Help | Prefix: `" + Main.getPrefix() + "`")
					.setDescription("The commands of " + event.getJDA().getSelfUser().getName() + " are listed below. To access detailed help for a command, send `" + Main.getPrefix() + "{command} help`.")
					.addField("Purging Messages:", "`" + Main.getPrefix() + "purge [# of messages]`", false)
					.addField("Issuing Strikes:", "`" + Main.getPrefix() + "strike [target ID] <reason>`", false)
					.addField("Muting Members:", "`" + Main.getPrefix() + "mute [target ID] [duration + timeunit] <reason>`", false)
					.addField("Kicking Members:", "`" + Main.getPrefix() + "kick [target ID] <reason>`", false)
					.addField("Banning Members:", "`" + Main.getPrefix() + "ban [target ID] <reason>`", false)
					.addField("Banning Members/Purging Perpetrator's Messages:", "`" + Main.getPrefix() + "pban [target ID] <reason>`", false)
					.addField("Calling the General Help Menu:", "`" + Main.getPrefix() + "help`", false)
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl())
					.setTimestamp(Instant.now());
			
			event.getChannel().sendMessageEmbeds(help.build()).queue();
		}
	}
}
