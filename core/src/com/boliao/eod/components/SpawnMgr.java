package com.boliao.eod.components;

import com.boliao.eod.GameObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 6/2/17.
 * todo: different spawn types
 */

public class SpawnMgr extends Component {
    private static final String TAG = "SpawnMgr:C";

    private List<GameObject> gameObjects = new LinkedList<GameObject>();

    public SpawnMgr() {
        super("SpawnMgr");
    }

    public GameObject spawn() {
        return null;
    }
}
