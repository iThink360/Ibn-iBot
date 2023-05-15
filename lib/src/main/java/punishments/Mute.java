/** Event listener for muting members. */

package punishments;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Mute extends ListenerAdapter
{
	private static final Color MUTE_EMBED = new Color(0xaaaaaa);
	private static final long TIMEOUT = 814366351623847937L;
	
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		if (!args[0].equalsIgnoreCase(Main.getPrefix() + "mute"))
			return;
		
		if (!sender.isOwner() && !sender.hasPermission(Permission.ADMINISTRATOR) && !sender.hasPermission(Permission.KICK_MEMBERS))
		{
			// Denied
			EmbedBuilder denied = new EmbedBuilder()
					.setColor(MUTE_EMBED)
					.setDescription("❌ " + sender.getUser().getAsTag() + ", nice try. You can't use that.");
			event.getChannel().sendMessageEmbeds(denied.build()).queue();
			return;
		}
		
		if (args.length <= 1)
			return;
		
		if (args[1].equals("@everyone"))
		{
			// Are You Sure?
			EmbedBuilder areYouSure = new EmbedBuilder()
					.setColor(MUTE_EMBED)
					.setDescription("‼ **Are You Sure?**");
			event.getChannel().sendMessageEmbeds(areYouSure.build()).queue();
			return;
		}
		
		if (args.length == 2 && args[1].equalsIgnoreCase("help"))
		{
			// Help
			EmbedBuilder help = new EmbedBuilder()
					.setColor(MUTE_EMBED)
					.setTitle("🔇 Mute")
					.setDescription("Shuts up a member with the Timeout role.")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.addField("Command:", "`" + Main.getPrefix() + "mute [target ID] [duration] [timeunit] <reason>`", false)
					.addField("`[target ID]`", "-The ID of the target who will be muted.\n-Omit [] when specifying this value.", true)
					.addField("`[duration] [timeunit]`", "-The duration of timeout.\n-Specify the amount and time unit; the available time units are **sec**, **min**, **hr**, and **day**.\n-USE A SPACE between the number and time unit. Omit [] for this field.", true)
					.addField("`<reason>`", "-The reason for the mute.\n-This field is optional. When specifying the reason, omit <>.", true)
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl());
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			return;
		}
		
		try
		{
			Member target = event.getGuild().retrieveMemberById(args[1]).complete();
			
			if (target.equals(sender))
			{
				// Muting Yourself?
				EmbedBuilder idiot = new EmbedBuilder()
						.setColor(MUTE_EMBED)
						.setDescription("❌ Use common sense; you can't mute yourself.");
				event.getChannel().sendMessageEmbeds(idiot.build()).queue();
			}
			else if (target.isOwner() || target.hasPermission(Permission.ADMINISTRATOR) || target.hasPermission(Permission.KICK_MEMBERS))
			{
				// Illegal Target
				EmbedBuilder error = new EmbedBuilder()
						.setColor(MUTE_EMBED)
						.setDescription("❌ You cannot mute this member.");
				event.getChannel().sendMessageEmbeds(error.build()).queue();
			}
			else
			{
				// Success
				int length = Integer.parseInt(args[2]);
				TimeUnit timeunit;
				
				switch (args[3].toLowerCase())
				{
					case "sec":
						timeunit = TimeUnit.SECONDS;
						break;
					case "min":
						timeunit = TimeUnit.MINUTES;
						break;
					case "hr":
						timeunit = TimeUnit.HOURS;
						break;
					case "day":
						timeunit = TimeUnit.DAYS;
						break;
					default:
						timeunit = TimeUnit.MINUTES;
						break;
				}
				
				EmbedBuilder mute = new EmbedBuilder()
						.setColor(MUTE_EMBED)
						.setDescription("🔇 **" + target.getUser().getAsTag() + "** was muted.")
						.setFooter("NOTE: If the bot shuts down, the perpetrator MUST be unmuted manually!");
				
				String reason = "";
				if (args.length > 4)
				{
					for (int i = 4; i < args.length - 1; i++)
						reason += args[i] + " ";
					reason += args[args.length - 1];
				}
				
				event.getGuild().addRoleToMember(target, event.getGuild().getRoleById(TIMEOUT)).queue();
				event.getGuild().removeRoleFromMember(target, event.getGuild().getRoleById(TIMEOUT)).queueAfter(length, timeunit);
				
				event.getChannel().sendMessageEmbeds(mute.build()).queue();
				
				// DM the target
				EmbedBuilder DM = new EmbedBuilder()
						.setTitle("🔇 You have been muted.")
						.setColor(MUTE_EMBED)
						.addField("Duration:", length + " " + timeunit.toString(), false)
						.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
						.setFooter("REMINDER: Mutes imply strikes. This will count towards the 3 strikes that lead to server dismissal. For questions, please reach out to staff via the ModMail bot.");
				if (!reason.equals(""))
					DM.addField("Reason:", reason, false);
				target.getUser().openPrivateChannel().complete().sendMessageEmbeds(DM.build()).queue();
				
				// Log the mute
				EmbedBuilder log = new EmbedBuilder()
						.setTitle("🔇 Mute Log")
						.setColor(MUTE_EMBED)
						.addField("Moderator:", sender.getAsMention(), false)
						.addField("Perpetrator:", target.getAsMention(), true)
						.addField("Perpetrator (Raw Tag):", target.getUser().getAsTag(), true)
						.addField("Duration:", length + " " + timeunit.toString(), false)
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
					.setColor(MUTE_EMBED)
					.setTitle("❗ Mute Error")
					.setDescription("You may have used the command incorrectly. Please try again. For help on muting members, send `" + Main.getPrefix() + "mute help`.")
					.addField("Exception Thrown:", e.toString(), false);
			event.getChannel().sendMessageEmbeds(error.build()).queue();
		}
	}
}
