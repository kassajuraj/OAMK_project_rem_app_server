package main_Server;

/**
 * 
 * @author juraj
 *
 */
public class main_rem_app_server {

	public static void main(String[] args) {
		server s = new server();
		s.makeEasyGuiForServer();
		System.out.println("**************************************");
		System.out.println("*           Remem_app - server         *");
		System.out.println("*                    /\\                                     *");
		System.out.println("*            ___/  \\        /\\_____              *");
		System.out.println("*                         \\   /                             *");
		System.out.println("*                           \\/                              *");
		System.out.println("**************************************");
		
		
		s.MonitoringNotifications();
		s.runServer();

	}

}
