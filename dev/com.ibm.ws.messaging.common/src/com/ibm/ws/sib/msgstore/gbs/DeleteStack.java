package com.ibm.ws.sib.msgstore.gbs;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason        Date     Origin   Description
 * ------------- -------- -------- --------------------------------------------
 * 176001        09/09/03 corrigk  Original
 * 461337        22/08/07 gareth   FINDBUGS: update sib.msgstore exclusions 
 * ============================================================================
 */

/**
 * A stack of remembered nodes used for delete processing.
 *
 * <p>See the parent NodeStack for more detailed information.</p>
 *
 * @author Stewart L. Palmer
 */
public class DeleteStack extends NodeStack
{
  /**
   * This class is used during fringe rebalancing.  See GBSDeleteFringe.
   */
  static class Linearizer
  {
    GBSNode   lastp;               /* Prior value of p                      */
    GBSNode   headp;               /* Head of new linear list               */
    int       depth;               /* Current recursion depth               */

    private void reset()
    {
      lastp = null;
      headp = null;
      depth = 0;
    }
  }

  /**
   * This class is used during height rebalancing.  See GBSDeleteHeight.
   */
  static class HeightNote
  {
    boolean   depthDecrease;
    GBSNode   bnew;

    private void reset()
    {
      depthDecrease = false;
      bnew = null;
    }
  }

  /**
   * This class is used during fringe rebalancing.  See GBSDeleteFringe.
   */
  static class FringeNote
  {
    boolean     depthDecrease;
    boolean     conditionalDecrease;
    int         conditionalBalance;
    GBSNode     newg;
    GBSNode     newf;

    private void reset()
    {
      depthDecrease = false;
      conditionalDecrease = false;
      conditionalBalance = 0;
      newg = null;
      newf = null;
    }
  }

  DeleteStack(
    GBSTree      tree)
  {
    super(tree);
    _linearizer = new Linearizer();
    _heightNote = new HeightNote();
    _fringeNote = new FringeNote();
    _insertStack = new InsertStack(tree);
    _deleteNode = new DeleteNode();
  }

  /**
   * Reset to post-construction state.
   */
  public void reset()
  {
    super.reset();
    _linearizer.reset();
    _heightNote.reset();
    _fringeNote.reset();
    _insertStack.reset();
    _deleteNode.reset();
  }

  Linearizer linearizer()
  { return _linearizer; }

  HeightNote heightNote()
  { return _heightNote; }

  FringeNote fringeNote()
  { return _fringeNote; }

  InsertStack insertStack()
  { return _insertStack; }

  DeleteNode deleteNode()
  { return _deleteNode; }

  /**
   * Migrate a hole through to the end of a fringe.
   */
  public boolean processNode(
    GBSNode       p)
  {
    boolean done = false;

    lastNode().addRightMostKey(p.leftMostKey());
    p.overlayLeftShift(0);
    if ( !(p.isFull()) ) {
      done = true;
    }
    p.decrementPopulation();

    return done;
  }

  /**
   * Start the stack with the root of the tree.
   */
  void start(
    GBSNode     node,
    String      starter)
  {
    super.stackStart(node, starter);
  }

  /**
   * Add a node and associated state to the end of the stack without pushing.
   */
  void add(
    int         state,
    GBSNode     node)
  {
    _state[index()+1] = state;
    _node[index()+1]  = node;
  }

  void setState(
    int       x,
    int       state)
  {
    if (x < 0) {
      throw new RuntimeException("x < 0");
    }
    if (x > index()) {
      throw new RuntimeException("x = " + x + ", index() = " + index());
    }
    switch(state)
    {
      case NodeStack.VISIT_LEFT:
        break;
      case NodeStack.PROCESS_CURRENT:
        break;
      case NodeStack.VISIT_RIGHT:
        break;
      case NodeStack.DONE_VISITS:
        break;
      default:
        throw new RuntimeException("Help!, state = " + state + ".");
    }
    _state[x] = state;
  }

  private Linearizer   _linearizer;
  private HeightNote   _heightNote;
  private FringeNote   _fringeNote;
  private InsertStack  _insertStack;
  private DeleteNode   _deleteNode;
}
