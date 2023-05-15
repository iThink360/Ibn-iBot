/** Event listener for banning members. */

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

public class Ban extends ListenerAdapter
{
	private static final Color BAN_EMBED = new Color(0xff0000);
	
	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		Member sender = event.getMember();
		
		if (!args[0].equalsIgnoreCase(Main.getPrefix() + "ban") && !args[0].equalsIgnoreCase(Main.getPrefix() + "pban"))
			return;
		
		if (!sender.isOwner() && !sender.hasPermission(Permission.ADMINISTRATOR) && !sender.hasPermission(Permission.BAN_MEMBERS))
		{
			// Denied
			EmbedBuilder denied = new EmbedBuilder()
					.setColor(BAN_EMBED)
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
					.setColor(BAN_EMBED)
					.setTitle("⛔ Ban")
					.setDescription("Bans a member. If desired, the member's messages can also be purged. Use the appropriate command according to your needs.")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.addField("Standard Ban Command:", "`" + Main.getPrefix() + "ban [target ID] <reason>`", false)
					.addField("Purge Ban Command:", "`" + Main.getPrefix() + "pban [target ID] <reason>`", false)
					.addField("`[target ID]`", "-The ID of the target who will get banned.\n-Omit [] when specifying this value.", true)
					.addField("`<reason>`", "-The reason for the ban.\n-This field is optional. When specifying the reason, omit <>.", true)
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl());
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			return;
		}
		
		if (args[1].equals("@everyone") || args[1].equals("@here"))
		{
			// Are You Sure?
			EmbedBuilder areYouSure = new EmbedBuilder()
					.setColor(BAN_EMBED)
					.setDescription("‼ **Are You Sure?**");
			event.getChannel().sendMessageEmbeds(areYouSure.build()).queue();
			return;
		}
		
		try
		{
			Member target = event.getGuild().retrieveMemberById(args[1]).complete();
			
			if (target.equals(sender))
			{
				// Banning Yourself?
				EmbedBuilder idiot = new EmbedBuilder()
						.setColor(BAN_EMBED)
						.setDescription("❌ Use common sense; you can't ban yourself.");
				event.getChannel().sendMessageEmbeds(idiot.build()).queue();
			}
			else if (target.isOwner() || target.hasPermission(Permission.ADMINISTRATOR) || target.hasPermission(Permission.KICK_MEMBERS))
			{
				// Illegal Target
				EmbedBuilder error = new EmbedBuilder()
						.setColor(BAN_EMBED)
						.setDescription("❌ You cannot ban this member.");
				event.getChannel().sendMessageEmbeds(error.build()).queue();
			}
			else
			{
				// Success
				EmbedBuilder ban = new EmbedBuilder()
						.setColor(BAN_EMBED)
						.setDescription("⛔ **" + target.getUser().getAsTag() + "** was banned.");

				String reason = "";
				if (args.length > 2)
				{
					for (int i = 2; i < args.length - 1; i++)
						reason += args[i] + " ";
					reason += args[args.length - 1];
				}
				
				if (args[0].equalsIgnoreCase(Main.getPrefix() + "ban"))
				{
					target.ban(0, reason).queue();
					logAndDM(event, target, reason, false);
				}
				else if (args[0].equalsIgnoreCase(Main.getPrefix() + "pban"))
				{
					try
					{
						// pban attempt
						target.ban(7, reason).queue();
						logAndDM(event, target, reason, true);
					}
					catch (Exception e)
					{
						// pban fail, resort to normal ban
						target.ban(0, reason).queue();
						logAndDM(event, target, reason, false);
					}
				}
				
				event.getChannel().sendMessageEmbeds(ban.build()).complete();
			}
		}
		catch (Exception e)
		{
			// Error
			EmbedBuilder error = new EmbedBuilder()
					.setColor(BAN_EMBED)
					.setTitle("❗ Ban Error")
					.setDescription("You may have used the command incorrectly. Please try again. For help on banning/purge banning, send `" + Main.getPrefix() + "ban help` or `" + Main.getPrefix() + "pban help`.")
					.addField("Exception Thrown:", e.toString(), false);
			event.getChannel().sendMessageEmbeds(error.build()).queue();
		}
	}
	
	private static void logAndDM(GuildMessageReceivedEvent event, Member target, String reason, boolean pban)
	{
		// DM the target
		EmbedBuilder DM = new EmbedBuilder()
				.setTitle("⛔ You have been banned.")
				.setColor(BAN_EMBED)
				.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
		if (!reason.equals(""))
			DM.addField("Reason:", reason, false);
		target.getUser().openPrivateChannel().complete().sendMessageEmbeds(DM.build()).queue();
		
		// Log it
		EmbedBuilder log = new EmbedBuilder()
				.setTitle("⛔ Ban Log")
				.setColor(BAN_EMBED)
				.addField("Moderator:", event.getAuthor().getAsMention(), false)
				.addField("Perpetrator:", target.getAsMention(), true)
				.addField("Perpetrator (Raw Tag):", target.getUser().getAsTag(), true)
				.setFooter("Perpetrator ID: " + target.getId())
				.setTimestamp(Instant.now());
		if (!reason.equals(""))
			log.addField("Reason:", reason, false);
		log.addField("Purge Ban:", (pban) ? "Yes" : "No", false);
		Main.getMglInfractions().sendMessageEmbeds(log.build()).queue();
	}
}
