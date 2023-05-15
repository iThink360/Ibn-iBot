/** The main class for Ibn iBot. */

/* FOR REFERENCE:
 * 
 * #moderation:		815332564575322162
 * #administration:	766045053465919508
 */

package main;

import java.awt.Color;
import java.util.EnumSet;

import javax.security.auth.login.LoginException;

import commands.Discontinue;
import commands.Help;
import commands.ModifyProfile;
import commands.Purge;
import events.Automoderator;
import events.BreakInfiniteMessageLoop;
import events.LinkFilter;
import events.SuppressEveryoneAndHere;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import punishments.Ban;
import punishments.Kick;
import punishments.Mute;
import punishments.Strike;

public class Main
{
	/* Initialize instance variables */
	private static JDA jda;
	
	private static final String PREFIX = "i-->";
	private static final long ITK = 855481499774877756L;
	
	private static final long MGL = 765990029923057664L;
	private static final long MGL_INFRACTIONS = 768076721291001866L;
	private static final long MGL_LOUNGE = 766887320229117953L;
	private static final long MGL_MODERATION = 815332564575322162L;
	private static final long MGL_ADMINISTRATION = 766045053465919508L;
	private static final Color DEFAULT_EMBED = new Color(0x6a29cc);
	
	public static void main(String[] args) throws LoginException
	{
		/** JDABuilder Instantiation */
		jda = JDABuilder.createDefault("ODYwODUyODE5NTE0MjI4NzM2.YOBRqg.xjolM5ZrDxveY5nWCAmKvLOlow0")
				.enableIntents(EnumSet.allOf(GatewayIntent.class))
				.enableCache(EnumSet.allOf(CacheFlag.class))
				.build();
		
		/** Event Listeners */
		
		// Main
		jda.addEventListener(new Ready());
		jda.addEventListener(new Kill());
		
		// Commands
		jda.addEventListener(new Purge());
		jda.addEventListener(new ModifyProfile());
		jda.addEventListener(new Help());
		jda.addEventListener(new Discontinue());

		// Punishments
		jda.addEventListener(new Strike());
		jda.addEventListener(new Kick());
		jda.addEventListener(new Ban());
		jda.addEventListener(new Mute());
		
		// Events
		jda.addEventListener(new Automoderator());
		jda.addEventListener(new BreakInfiniteMessageLoop());
		jda.addEventListener(new SuppressEveryoneAndHere());
		jda.addEventListener(new LinkFilter());
	}
	
	/* Accessor functions */
	
	public static String getPrefix()
	{
		return PREFIX;
	}
	
	public static Guild getMgl()
	{
		return jda.getGuildById(MGL);
	}
	
	public static TextChannel getMglInfractions()
	{
		return getMgl().getTextChannelById(MGL_INFRACTIONS);
	}
	
	public static TextChannel getMglLounge()
	{
		return getMgl().getTextChannelById(MGL_LOUNGE);
	}
	
	public static TextChannel getMglModeration()
	{
		return getMgl().getTextChannelById(MGL_MODERATION);
	}
	
	public static TextChannel getMglAdministration()
	{
		return getMgl().getTextChannelById(MGL_ADMINISTRATION);
	}
	
	public static User getOwner()
	{
		// Use Ibn iBot Support ID
		return jda.getGuildById(860851990887137290L).retrieveMemberById(ITK).complete().getUser();
	}
	
	public static Color getDefaultEmbedColor()
	{
		return DEFAULT_EMBED;
	}
	
	public static String[] getResignationMessage()
	{
		String partOne = "**__Dated May 15, 2021 - From the Desk of Former Legend iThink360__**\n\n"
				+ "Salam alaikum wa rahmatullahi wa barakatuh everyone.\n\n"
				+ "Alhumdulillahi rabbil ‘alameen. I thank Allah for all the favors He has bestowed upon me, upon you, upon all of us. I cannot emphasize how fortunate it has been for me to be able to contribute to such a wonderful concept and help MGL grow to what it has become today.\n\n"
				+ "It feels like yesterday I was just a member and the server was nearly empty; merely at around 600 members. However, ever since, MGL has experienced significant growth, mashallah. Not only does MGL have a system running, but we have a wonderful team of moderators, and now alhumdulillah super moderators, that are working tirelessly to hold down the fort and serve as spectators of the Discord server, MGL’s central home.\n\n"
				+ "I do apologize for being inactive lately. Unfortunately, however, I come with some important news. As time has progressed, it has become very hard for me to donate time to MGL; even the slightest bit is nearly impossible for me to do. I now have a lot of priorities to dedicate myself to in my life, such as my school, my studies and education, my quran, my extra activities, my personal time with others, my personal entertainment, my future, and so on and so forth. As time progresses and my responsibilities continue to increase, paired with the fact that now, alhumdulillah, the moderation team now has the server under control, I am afraid to say that my time here is up.";
		
		String partTwo = "Alhumdulillah, I have donated 8 months of effort to MGL. It feels like yesterday @Din and I were the only Moderators; after which we advanced forward to King Moderators, then achieved Level I Administration, before getting debunked back to King Moderation but finally attaining the Legendary position in MGL. It has been a bumpy ride, and while there have been a lot of ups and downs… from simple mistakes and trolls to a whole server nuke and lockdown… alhumdulillah it has not only been a blessing from Allah, but it has also been a privilege granted by Yusha. Without his approval for me serving as a staff member, and without the teamwork of all of our moderation team today, none of what I have contributed would ever have been possible. I sincerely thank Allah for granting me the opportunity to be an MGL staff member, and I sincerely thank the rest of you guys for being awesome contributors.\n\n"
				+ "Like I said, however, my time is now up and it’s time for me to move on from serving here as it is now time for me to focus on myself, start to devote my attention to bigger responsibilities and important matters that lay forward for me in my own life. I pray that Allah makes each one of you guys successful in your lives and helps MGL continue to thrive for years to come.\n\n"
				+ "**With this being said, I, @iThink360, hereby resign the @Legend position in Muslim Gamer’s League.**\n\n"
				+ "May Allah forgive our sins and save us from Hell. Ameen. God willing, I hope to see us united in Jannah where our ultimate destination will be. Ameen."
				+ "That’s it from me. From me to you now, salam alaikum wa rahmatullahi wa barakatuh.";
		
		String[] result = {partOne, partTwo};
		return result;
	}
}
