/** Event listener for issuing strikes. */

package punishments;

import java.awt.Color;
import java.time.Instant;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Strike extends ListenerAdapter
{
	private static final Color STRIKE_EMBED = new Color(0xffff00);
	
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		if (!args[0].equalsIgnoreCase(Main.getPrefix() + "strike"))
			return;
		
		if (!sender.isOwner() && !sender.hasPermission(Permission.ADMINISTRATOR) && !sender.hasPermission(Permission.KICK_MEMBERS))
		{
			// Denied
			EmbedBuilder denied = new EmbedBuilder()
					.setColor(STRIKE_EMBED)
					.setDescription("❌ " + sender.getUser().getAsTag() + ", nice try. You can't use that.");
			event.getChannel().sendMessageEmbeds(denied.build()).queue();
			return;
		}
		
		if (args.length <= 1)
			return;
		
		if (args[1].equalsIgnoreCase("help"))
		{
			// Help
			EmbedBuilder help = new EmbedBuilder()
					.setColor(STRIKE_EMBED)
					.setTitle("⚠ Strike")
					.setDescription("Issues a member 1 strike.")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.addField("Command:", "`" + Main.getPrefix() + "strike [target ID] <reason>`", false)
					.addField("`[target ID]`", "-The ID of the target who will receive the strike.\n-Omit [] when specifying this value.", true)
					.addField("`<reason>`", "-The reason for the strike.\n-This field is optional. When specifying the reason, omit <>.", true)
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl());
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			return;
		}
		
		if (args[1].equals("@everyone"))
		{
			// Are You Sure?
			EmbedBuilder areYouSure = new EmbedBuilder()
					.setColor(STRIKE_EMBED)
					.setDescription("‼ **Are You Sure?**");
			event.getChannel().sendMessageEmbeds(areYouSure.build()).queue();
			return;
		}
		
		try
		{
			Member target = event.getGuild().retrieveMemberById(args[1]).complete();
			
			if (target.equals(sender))
			{
				// Striking Yourself?
				EmbedBuilder idiot = new EmbedBuilder()
						.setColor(STRIKE_EMBED)
						.setDescription("❌ Use common sense; you can't strike yourself.");
				event.getChannel().sendMessageEmbeds(idiot.build()).queue();
			}
			else if (target.isOwner() || target.hasPermission(Permission.ADMINISTRATOR) || target.hasPermission(Permission.KICK_MEMBERS))
			{
				// Illegal Target
				EmbedBuilder error = new EmbedBuilder()
						.setColor(STRIKE_EMBED)
						.setDescription("❌ You cannot issue this member a strike.");
				event.getChannel().sendMessageEmbeds(error.build()).queue();
			}
			else
			{
				// Success
				EmbedBuilder strike = new EmbedBuilder()
						.setColor(STRIKE_EMBED)
						.setDescription("⚠ **" + target.getUser().getAsTag() + "** was struck.");
				
				String reason = "";
				if (args.length > 2)
				{
					for (int i = 2; i < args.length - 1; i++)
						reason += args[i] + " ";
					reason += args[args.length - 1];
				}
				
				event.getChannel().sendMessageEmbeds(strike.build()).queue();
				
				// DM the target
				EmbedBuilder DM = new EmbedBuilder()
						.setTitle("⚠ You have received a strike.")
						.setColor(STRIKE_EMBED)
						.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
						.setFooter("REMINDER: A maximum of 3 strikes or an extreme infraction will lead to server dismissal. For questions, please reach out to staff via the ModMail bot.");
				if (!reason.equals(""))
					DM.addField("Reason:", reason, false);
				target.getUser().openPrivateChannel().complete().sendMessageEmbeds(DM.build()).queue();
				
				// Log the strike
				EmbedBuilder log = new EmbedBuilder()
						.setTitle("⚠ Strike Log")
						.setColor(STRIKE_EMBED)
						.addField("Moderator:", sender.getAsMention(), false)
						.addField("Perpetrator:", target.getAsMention(), true)
						.addField("Perpetrator (Raw Tag):", target.getUser().getAsTag(), true)
						.setFooter("Perpetrator ID: " + target.getId())
						.setTimestamp(Instant.now());
				if (!reason.equals(""))
					log.addField("Reason:", reason, false);
				Main.getMglInfractions().sendMessageEmbeds(log.build()).queue();
			}
		}
		catch (Exception e)
		{
			// Error
			EmbedBuilder error = new EmbedBuilder()
					.setColor(STRIKE_EMBED)
					.setTitle("❗ Strike Error")
					.setDescription("You may have used the command incorrectly. Please try again. For help on issuing strikes, send `" + Main.getPrefix() + "strike help`.")
					.addField("Exception Thrown:", e.toString(), false);
			event.getChannel().sendMessageEmbeds(error.build()).queue();
		}
	}
}
