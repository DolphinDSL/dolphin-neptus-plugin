package pt.lsts.neptus.plugins.dolphin.dsl;

import pt.lsts.imc.IMCMessage
import pt.lsts.dolphin.runtime.NodeSet
import pt.lsts.dolphin.runtime.Node
import pt.lsts.neptus.comm.manager.imc.ImcMsgManager
import pt.lsts.neptus.plugins.dolphin.*
import pt.lsts.neptus.types.mission.plan.PlanType
import pt.lsts.neptus.comm.IMCSendMessageUtils
import pt.lsts.neptus.comm.IMCUtils
import pt.lsts.imc.PlanDB
import pt.lsts.imc.PlanDB.OP
import pt.lsts.imc.PlanDB.TYPE
/**
 * 
 * @author Keila
 * 
 * Dolphin additional instructions to program 
 *
 */
class Instructions {
  
   /**
    * Gets IMC plan from Neptus Main Console 
    * @param id Name of the plan
    * @return
    */
  static IMCPlanTask imcPlan(String id) {
    NeptusPlatform.INSTANCE.getPlatformTask(id)
  }
  
  /**
   * Generates IMC plan and adds it to the Neptus Main Console
   * @param cl closure with IMC DSL specification 
   * @return
   */
  static IMCPlanTask imcPlan(Closure cl) {
      def dslPlan = new DSLPlan()

      def code = cl.rehydrate(dslPlan, cl.getOwner(), cl.getThisObject())
      code.resolveStrategy = Closure.DELEGATE_FIRST
      code()
      def ps = dslPlan.asPlanSpecification()
      NeptusPlatform.INSTANCE.storeInConsole(ps)
      new IMCPlanTask(ps)
  }
  
  static void sendMessage(NodeSet nodes, IMCMessage message) {
     for (Node n : nodes) {
       NeptusPlatform.INSTANCE.displayMessage 'Sending \'%s\' to \'%s\'', 
                                               message.getAbbrev(), 
                                               n.getId()
       ImcMsgManager.getManager().sendMessageToSystem message,
                                                      n.getId()         
     }
  }  
  
     /**
      * Store IMC plan in the Node's database
      * @param nodes Set of Nodes
      * @param task IMC plan 
      */
     static void storePlan(NodeSet nodes, IMCPlanTask task) {
         def message = new PlanDB(TYPE.REQUEST,OP.SET,IMCSendMessageUtils.getNextRequestId(),task.id,task.getPlanSpecification(),"NVL Task")

                  
         for (Node n : nodes) {
           NeptusPlatform.INSTANCE.displayMessage 'Sending \'%s\' to \'%s\'',
                                                   task.id,
                                                   n.getId()
           ImcMsgManager.getManager().sendMessageToSystem message,
                                                          n.getId()
         }
  }
  
  static main(args) {
    NeptusPlatform.INSTANCE.displayMessage 'Neptus language instructions extensions loaded!'
  }
}

