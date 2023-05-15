package commands;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Discontinue extends ListenerAdapter
{
	@Override
	public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event)
	{
		if (event.getAuthor().isBot())
			return;
		
		if (event.getAuthor().equals(Main.getOwner()) && event.getMessage().getContentRaw().equalsIgnoreCase(Main.getPrefix() + "discontinue"))
		{
			/* Step 1 - Initial Discontinuity Notification */
			EmbedBuilder alert = new EmbedBuilder()
					.setColor(0xff0000)
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.setTitle("WARNING: DISCONTINUITY ALERT ❌")
					.setDescription("Just a friendly reminder that " + event.getJDA().getSelfUser().getName() + " will **__BE PERMANENTLY DISCONTINUED EFFECTIVE 1 hour from right now.__**");
			Main.getMglLounge().sendMessage("@everyone").setEmbeds(alert.build()).queue();
			
			/* Step 2 - Leaving iThink360's Resignation Message */
			EmbedBuilder resignationNotification = new EmbedBuilder()
					.setColor(0xff0000)
					.setTitle("Exiting " + Main.getMgl().getName() + " soon...")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.setDescription("However, just to leave you all with a little something... Below is iThink360's resignation message that he sent to his fellow staff members before resigning on May 15, 2021. Annoyingly, he was unable to share his resignation message with @everyone the day of, since someone spoke on behalf of him.");
			
			Main.getMglLounge().sendMessageEmbeds(resignationNotification.build()).queue();
			Main.getMglLounge().sendMessage(Main.getResignationMessage()[0]).queueAfter(5, TimeUnit.SECONDS);
			Main.getMglLounge().sendMessage(Main.getResignationMessage()[1]).queueAfter(5, TimeUnit.SECONDS);
			
			/* Step 3 - Expose Medo */
			EmbedBuilder expose = new EmbedBuilder()
					.setColor(0xff0000)
					.setTitle("If there's anything last I want to say before I leave, here's a message on behalf of former MGL Legend iThink360...")
					.setDescription("It has been a great year from late 2020 to early 2021 serving the " + Main.getMgl().getName() + ". However, a reason that I also resigned that was initially not addressed to anyone regarded the pressures from higher level staff that I had to face. Although I was a Legend at the time, there were some moments where I felt that I was being behaved with arrogantly; I was behaved disrepectfully with; I was severely undervalued; and I was mistreated and used instead of engaged with in an appropriate manner. The pressures and stress from such tense situations between myself and higher level staff - namely an administrator who has been recently promoted to an MGL Founder - was one of the reasons that I forced myself to resign. Nobody else knew about this beforehand, but since Ibn iBot is now exiting MGL, I decided to let this secret out to @everyone.")
					.setFooter("And, by the way, if the administrator who misbehaved with me in the ways I just addressed in this embed is reading this message, then you know who you are. Even after multiple confrontations, you still didn't change.");
			Main.getMglLounge().sendMessageEmbeds(expose.build()).queueAfter(5, TimeUnit.SECONDS);
			
			/* Step 4 - 5 Second Countdown */
			EmbedBuilder countdown = new EmbedBuilder()
					.setColor(0xff0000)
					.setTitle("Exiting " + Main.getMgl().getName() + " soon...");
			for (int i = 5; i > 0; i--)
			{
				countdown.setDescription("T-minus " + i + (i != 1 ? " seconds..." : " second..."));
				if (i == 4)
					countdown.setTitle(null);
				Main.getMglLounge().sendMessageEmbeds(countdown.build()).queueAfter(5 + (5-i), TimeUnit.SECONDS);
			}
			
			/* Step 5 - Final Discontinuity Notification */
			EmbedBuilder discontinued = new EmbedBuilder()
					.setColor(0xff0000)
					.setTitle("Initiating " + event.getJDA().getSelfUser().getName() + " Discontinuity...")
					.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
					.setDescription("Process successful. 👋\n**Shutting down now...**");
			Main.getMglLounge().sendMessageEmbeds(discontinued.build()).queueAfter(5 + 6, TimeUnit.SECONDS);
			
			/* Step 6 - Execute Operation Discontinue Ibn iBot */
			Main.getMgl().leave().queueAfter(15, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent event)
	{
		if (!event.getGuild().equals(Main.getMgl()))
			return;
		
		// If Ibn iBot exits MGL, fire a success notification to bot owner iTK
		EmbedBuilder completed = new EmbedBuilder()
				.setColor(0x00ff00)
				.setTitle("Discontinuity Process Successful! ✅");
		Main.getOwner().openPrivateChannel().complete().sendMessageEmbeds(completed.build()).queue();
	}
}
