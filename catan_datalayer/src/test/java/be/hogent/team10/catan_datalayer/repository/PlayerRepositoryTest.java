/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.repository;

import be.hogent.team10.catan_businesslogic.model.Player;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;

/**
 *
 * @author HP
 */
/**
 * public class PlayerRepositoryTest extends TestCase {
 *
 * private PlayerRepository repo; private String defaultName =
 * "PLAYERREPOSITORYTEST"; private String updatedName = "NAMEMODIFIED";
 *
 * public PlayerRepositoryTest(String testName) { super(testName); }
 *
 * @Override protected void setUp() throws Exception { super.setUp(); repo = new
 * PlayerRepository();
 *
 * }
 *
 * @Override protected void tearDown() throws Exception { super.tearDown(); }
 *
 * public void testAddPlayerDeletePlayer() { Player p = null; try {
 *
 * p = new Player(defaultName, defaultName + "
 * @" + defaultName + ".be", defaultName); repo.addPlayer(p); p =
 * repo.login(defaultName, defaultName); repo.deletePlayer(p);
 *
 * } catch (Exception e) { fail(); } /* try { assertNotNull(p);
 * repo.getPlayer(p.getId()); // fail(); } catch (Exception e) { } }
 *
 * }
 */
/**
 *
 * @throws Exception One method because id of inserted item is unknown.
 */
/*
 public void testLoginGetPlayerUpdatePlayerDeletePlayer() throws Exception {
 Player p = null;
 try {
 p = new Player(defaultName, defaultName + "@" + defaultName + ".be", defaultName);
 repo.addPlayer(p);
 p = repo.login(defaultName, defaultName);
 assertNotNull(p);
 p.setName(updatedName);
 p.setPassword(updatedName);
 repo.updatePlayer(p);
 p = repo.getPlayer(p.getId());
 assertNotNull(p);
 assertEquals(updatedName, p.getName());
 p = repo.login(updatedName, updatedName);
 assertNotNull(p);
 repo.deletePlayer(p);
 System.out.println(p.getId());

 } catch (Exception e) {
 System.err.println(e.getMessage());
 //fail("exception");
 }

 }
 }*/
