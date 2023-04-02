package com.opengg.loader.game.nu2.ai;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector3f;
import com.opengg.loader.BrickBench;
import com.opengg.loader.Util;
import com.opengg.loader.game.nu2.NU2MapComponent;
import com.opengg.loader.game.nu2.NU2MapData;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.List;

public class AI2Loader{

    public static final Map<String, List<AILocator>> locatorSets = new HashMap<>();

    public static void load(ByteBuffer fileData, NU2MapData mapData) {
        System.out.println("start loadfrsodkjbaoihidsa "+fileData.capacity() + ","+fileData.limit());
        NU2MapComponent.connections.clear();
        fileData.order(ByteOrder.LITTLE_ENDIAN);
        int version = fileData.getInt();
        System.out.println("version " + version);
        mapData.ai().version().set(version);
        int pathCount = fileData.getInt();
     //   System.out.println("AI Version: " + version);
       // System.out.println("PathCount:"+pathCount);
        for (int i = 0; i < pathCount; i++) {
            var pathName =  Util.getStringFromBuffer(fileData,16);
       //     System.out.println("AIOBJECT3 NAME: " + pathName);

            int numPathPoints = Byte.toUnsignedInt(fileData.get());
            int field0x11 = Byte.toUnsignedInt(fileData.get());

            int cnxCount;
            if (version == 1) {
                cnxCount = Byte.toUnsignedInt(fileData.get());
            } else {
                cnxCount = Short.toUnsignedInt(fileData.getShort());
            }

            AIPath path = new AIPath(pathName,new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            System.out.println("cnx change: " + cnxCount + "," + fileData.position());
            for (int j = 0; j < cnxCount; j++) {
                int aNode = Byte.toUnsignedInt(fileData.get());
                int bNode = Byte.toUnsignedInt(fileData.get());
             //   System.out.println("Connection: " + aNode + "," + bNode);
                int subObj10x0;
                int subObj10x4;
                if (version < 0xc) {
                    if (version < 9) {
                        subObj10x0 = Byte.toUnsignedInt(fileData.get());
                        subObj10x4 = Byte.toUnsignedInt(fileData.get());
                    } else {
                        subObj10x0 = Short.toUnsignedInt(fileData.getShort());
                        subObj10x4 = Short.toUnsignedInt(fileData.getShort());
                    }
                } else {
                    subObj10x0 = fileData.getInt();
                    subObj10x4 = fileData.getInt();
                }
                System.out.println(Integer.toBinaryString(subObj10x0)+","+Integer.toBinaryString(subObj10x4));
                int subObj10x8 = subObj10x0;
                int subObj10xc = subObj10x4;
                int subObj10x12 = Short.toUnsignedInt(fileData.getShort());
                int subObj10x14 = Short.toUnsignedInt(fileData.getShort());
                System.out.println(Util.shortAngleToFloat((short)subObj10x12) + "," + ","+ Integer.toBinaryString(subObj10x14));
                float subObj10x18 = fileData.getFloat();
                float subObj10x1c = fileData.getFloat();
               // System.out.println("Bitfield: " + Integer.toBinaryString(subObj10x14));
                //if(subObj10x14 != 0) JOptionPane.showInputDialog("sdds");
                //subObject1List.add(new AIPath.Sub1(to,field0x11,subObj10x0,subObj10x4,subObj10x8,subObj10xc,subObj10x12,subObj10x14,subObj10x18,subObj10x1c));
                path.connections().add(new AIPath.AIPathConnection(path,aNode,bNode,subObj10x0,subObj10x4,subObj10x12,subObj10x14,subObj10x18,subObj10x1c));
            //    System.out.println("CNX: " + j+","+path.connections().get(j));
            }

            if (version == 1) {
                fileData.get();
            }
            //aipath
            System.out.println("starting read for " + numPathPoints + "," + fileData.position());
            for (int j = 0; j < numPathPoints; j++) {
                int numToRead = fileData.getInt();
                String objectNameProbably;
                if (numToRead != 0) {
                    objectNameProbably = Util.getStringFromBuffer(fileData,numToRead);
                } else {
                    objectNameProbably = "";
                }

                Vector3f position = new Vector3f(fileData.getFloat(), fileData.getFloat(), fileData.getFloat());
            //    BrickBench.pointsToView.add(position);
                var XZSize = fileData.getFloat();
                float minY;
                float maxY;
                if (version < 8) {
                    minY = position.y - 0.2f;
                    maxY = position.y + 0.2f;
                } else {
                    minY = fileData.getFloat();
                    maxY = fileData.getFloat();
                }
          //      System.out.println("\""+objectNameProbably+"\""+" | " + position + " | "+XZSize+" | " + j);
                //MapViewer.cubes.add(Tuple.of(position,new Vector3f(XZSize,minY,maxY)));

                int numConnections = Byte.toUnsignedInt(fileData.get());
                Byte.toUnsignedInt(fileData.get());
                //padding
                fileData.get();
                byte flag = fileData.get();
               // System.out.println("Flag: " + flag);
                int unknownShort = Short.toUnsignedInt(fileData.getShort());
                int unknown = version < 0x13 ? fileData.get() | 0xff : fileData.get();
       //         System.out.println("The unks," + flag+","+ unknownShort+","+unknown);

                int specialObjectLength = Byte.toUnsignedInt(fileData.get());
                String specialObjectName="";
                Vector3f specialObjectVector;
                if (specialObjectLength != 0) {
                    specialObjectName = Util.getStringFromBuffer(fileData,specialObjectLength);
                    //System.out.println(specialObjectName);
                    specialObjectVector = new Vector3f(fileData.getFloat(), fileData.getFloat(), fileData.getFloat());
                //    System.out.println("Option Special: " + specialObjectName + "," + specialObjectVector);
                   // MapViewer.textPoints.add(Tuple.of(specialObjectName,specialObjectVector));
                } else {
                    specialObjectName = "";
                    specialObjectVector = new Vector3f();
                }

                var aiNodeConnections = new ArrayList<AIPath.AIPathConnection>();
                List<Integer> a = new ArrayList<>();
                if (numConnections != 0) {
                    for (int k = 0; k < numConnections; k++) {
                        var sub1Index = fileData.getShort();
                       // System.out.println(j + " " + sub1Index);
                        //var what = subObject1List.get(sub1Index);
                        aiNodeConnections.add(path.connections().get(sub1Index));
                        //System.out.println(j + " |   " + what);
                        a.add((int) sub1Index);
                    }

                    //Likely padding for odd
                    if ((numConnections & 1) != 0) {
                        fileData.getShort();
                        //System.out.println("Shorty:" + fileData.getShort());
                    }
                }

                //Likely Padding
                short routeIDBitfield = 0;
                short exitNodeRoutIDBitfield = 0;
                if (4 < version) {
                    routeIDBitfield = fileData.getShort();
                    exitNodeRoutIDBitfield = fileData.getShort();
                    //System.out.println("extra v4: " + objectNameProbably + " " + j + " | " + Integer.toBinaryString(x) +  ","+Integer.toBinaryString(y));
                }
                path.pathPoints().add(new AIPath.AIPathPoint(path,objectNameProbably,position,j,XZSize,minY,maxY,aiNodeConnections,specialObjectName,specialObjectVector,routeIDBitfield,exitNodeRoutIDBitfield));
                //subObject2List.add(new AIPath.Sub2(objectNameProbably, position, null, specialObjectVector, sub2ListOfSub1));
                //calcextents
                int f = j;
                int f2 = i;
                /*
                OpenGG.asyncExec(()->{
                    var visual = new RenderComponent(new TextureRenderable(ObjectCreator.createCylinder(), Texture.ofColor(Color.GREEN)),new SceneRenderUnit.UnitProperties());
                    visual.setUpdateEnabled(false);
                    visual.setRenderDistance(10);
                    visual.setPositionOffset(position.add(new Vector3f(0,maxY-minY,0)).multiply(-1,1,1));
                    visual.setScaleOffset(new Vector3f(XZSize/2,maxY-minY,XZSize/2));
                    //visual.setEnabled(false);
                    WorldEngine.findEverywhereByName("map").get(0).attach(visual);
                    //if(f2 == 0)
                      //  WorldEngine.findEverywhereByName("map").get(0).attach(new TextBillboardComponent(f+"",position));
                });*/
            }

            /*
            OpenGG.asyncExec(()->{
                WorldEngine.getCurrent().attach(new RenderComponent(new TextureRenderable(ObjectCreator.createPointList(subObject2List.stream().map(e->e.position().multiply(-1,1,1)).toList()), Texture.ofColor(Color.GREEN)),new SceneRenderUnit.UnitProperties()));
            });*/

            //DEBUGGG__________________________--
            for (int j = 0; j < cnxCount; j++) {
                var connection = path.connections().get(j);
                Vector3f point1 =  path.pathPoints().get(connection.aNode()).pos().multiply(-1,1,1);
                Vector3f point2 =  path.pathPoints().get(connection.bNode()).pos().multiply(-1,1,1);
                System.out.println(connection +"   , " + point1.distanceTo(point2));
                var difference = Util.floatMod(Util.shortAngleToFloat((short) connection.perpendicularAngle()) - Math.toDegrees(Math.atan2(point1.z - point2.z,point1.x - point2.x)),360);
                var xzdistance = Math.sqrt(Math.pow(point2.x-point1.x,2)+Math.pow(point2.z-point1.z,2));
                System.out.println(point1.distanceTo(point2) + "," + xzdistance + "," + difference + "," + path.pathPoints().get(connection.aNode()).xzSize() + "," + path.pathPoints().get(connection.bNode()).xzSize());
            }
            //END DEBUGGGGGGGGGGGGGGGGG

            byte[][] defaultConnectionTable = new byte[numPathPoints][numPathPoints];
            for (int j = 0; j < numPathPoints; j++) {
                fileData.get(defaultConnectionTable[j]);
            }
            path.routes().add(new AIPath.AIRoute(path,defaultConnectionTable));

            if (4 < version) {
                int subObject3Count = Byte.toUnsignedInt(fileData.get());
             //   System.out.println("Mystery: " + subObject3Count);
                for (int j = 0; j < subObject3Count; j++) {
                    int size2 = Byte.toUnsignedInt(fileData.get());
               //     System.out.println("Internal Mystery: " + size2);
                    String routeName = "Unknown Route Name";
                    byte[][] routingTable = null;
                    byte[] pathPoints = null;
                    byte[] enterExitNodes = null;
                    if (size2 != 0) {
                        routeName = Util.getStringFromBuffer(fileData,size2);
                        System.out.println("Route: " + routeName);
                        int numRoutePathPoints = Byte.toUnsignedInt(fileData.get());
                        int soubBuffer4Size = Byte.toUnsignedInt(fileData.get());
                        fileData.get();
                        fileData.get();
                        if (numPathPoints != 0 && numRoutePathPoints != 0) {
                            var w2 =new byte[numPathPoints];
                            fileData.get(w2);
                            System.out.println(Arrays.toString(w2));
                            pathPoints = new byte[numRoutePathPoints];
                            fileData.get(pathPoints);
                            System.out.println(Arrays.toString(pathPoints));
                            System.out.println("------");

                            routingTable = new byte[numRoutePathPoints][numRoutePathPoints];
                            for (int k = 0; k < numRoutePathPoints; k++) {
                                fileData.get(routingTable[k]);
                                System.out.println(Arrays.toString(routingTable[k]));
                            }
                            System.out.println("s4");
                            if (soubBuffer4Size != 0) {
                                enterExitNodes = new byte[soubBuffer4Size];
                                fileData.get(enterExitNodes);
                                System.out.println(Arrays.toString(enterExitNodes));
                            }
                        }
                    }
                    List<String> characters = new ArrayList<>();
                    int anotherCount = Byte.toUnsignedInt(fileData.get());
                    for (int p = 0; p < anotherCount; p++) {
                        int byteVar = Byte.toUnsignedInt(fileData.get());
                        characters.add(Util.getStringFromBuffer(fileData,byteVar));
                    }
                    path.routes().add(new AIPath.AIRoute(path, routeName, j,pathPoints,routingTable,enterExitNodes,characters));
                }

            }
            //Path Exit Nodes
            if (0x12 < version) {
                int subObject4Count = Byte.toUnsignedInt(fileData.get());
                for (int j = 0; j < subObject4Count; j++) {
                    var nodeID = fileData.get();
                    var pathID = fileData.getShort();
                    System.out.println("AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH: "+Byte.toUnsignedInt(nodeID)+","+pathID);
                }
            }
            mapData.ai().paths().add(path);
        }


        if (0x12 < version) {
            int obj4Count = Short.toUnsignedInt(fileData.getShort());
            if (obj4Count != 0) {
                for (int i = 0; i < obj4Count; i++) {
                    int unk = Byte.toUnsignedInt(fileData.get());
                    if (unk != 0) {
                        for (int j = 0; j < unk; j++) {
                           System.out.print( fileData.get()+" | ");
                        }
                    }
                    System.out.println("\n");
                }
            }
        }

        //end of pathset

        int numTriggers = fileData.getInt();
        for (int i = 0; i < numTriggers; i++) {
            int filePos = fileData.position();
            var trigName = Util.getStringFromBuffer(fileData,16);
            var pos = new Vector3f(fileData.getFloat(), fileData.getFloat(), fileData.getFloat());
            var halfSize = new Vector3f(fileData.getFloat(), fileData.getFloat(), fileData.getFloat());
            float angle = Util.shortAngleToFloat(fileData.getShort());

            mapData.ai().triggers().add(new WorldTrigger(pos, halfSize, angle, trigName.trim(), filePos));

            byte one = fileData.get();
            byte two = fileData.get();
        }

        if(pathCount != 0){
            if(version >= 6){
                mapData.ai().locatorAddress().set(fileData.position());
                int aiLocatorCount = fileData.getInt();
                for (int i = 0; i < aiLocatorCount; i++) {
                    int filePos = fileData.position();
                    String locatorName = Util.getStringFromBuffer(fileData,16);
                    Vector3f pos = new Vector3f(fileData.getFloat(), fileData.getFloat(), fileData.getFloat());
                    var angle = fileData.getShort();
                    mapData.ai().aiLocators().add(new AILocator(pos, locatorName.trim(), i, Util.shortAngleToFloat(angle), filePos));
                    //Maybe path selector
                    var pathIDMaybe = fileData.get();
                    var unk = fileData.get();
                    //Connections Maybe?
                    var connectionsMaybe = fileData.getShort();
                    var float1 = fileData.getFloat();
                    var float2 = fileData.getFloat();

                    if(version >= 15){
                        fileData.getInt();
                    }
                }

                if(version >= 18){
                    mapData.ai().locatorSetAddress().set(fileData.position());
                    int aiLocatorSet = fileData.getInt();
                    for (int i = 0; i < aiLocatorSet; i++) {
                        int address = fileData.position();

                        String locatorRef = Util.getStringFromBuffer(fileData,16);
                        String locatorSetName = locatorRef.trim();

                        List<AILocator> locatorSet = new ArrayList<>();
                        locatorSets.put(locatorSetName, locatorSet);

                        int locatorCount = fileData.getInt();
                        for (int j = 0; j < locatorCount; j++) {
                            locatorSet.add(mapData.ai().aiLocators().get(Byte.toUnsignedInt(fileData.get())));
                        }
                        mapData.ai().aiLocatorSets().add(new AILocatorSet(locatorSetName, locatorSet, address, fileData.position()));
                    }
                }
            }

            mapData.ai().creatureStartAddress().set(fileData.position());
            int numCreatures = fileData.getInt();
            for (int i = 0; i < numCreatures; i++) {
                int fileAddress = fileData.position();

                var aiCharName = Util.getStringFromBuffer(fileData,16).trim();
                var scriptName = Util.getStringFromBuffer(fileData,16).trim();
                System.out.println(Integer.toHexString(fileAddress) + ","+ aiCharName + "," + Integer.toHexString(fileData.position()) + ","+fileData.capacity() + ","+fileData.limit());

                WorldTrigger trigger1 = null;
                WorldTrigger trigger2 = null;
                AILocator locator1 = null;
                AILocator locator2 = null;

                int textSize = version >= 14 ? 0x20 : 0x10;
                byte[] text = new byte[textSize];
                fileData.get(text);

                String charType = new String(text).substring(0, new String(text).indexOf('\0'));

                Vector3f startPoint = new Vector3f(fileData.getFloat(),fileData.getFloat(),fileData.getFloat());
                float startAngle = Util.shortAngleToFloat(fileData.getShort());
                if(version >= 16){
                    fileData.get();
                }
                fileData.get();
                fileData.get();
                fileData.getInt();
                fileData.getFloat();
                fileData.getFloat();
                fileData.getInt();
                fileData.get();
                fileData.get();
                fileData.getShort();
                if(version >= 3) {
                    fileData.getFloat();
                    fileData.getFloat();
                    fileData.getFloat();
                    fileData.getFloat();
                }
                if((version >= 4) && (fileData.getInt() != 0)){
                    String triggerRef = Util.getStringFromBuffer(fileData,16);
                    trigger1 = mapData.getTriggerByName(triggerRef.trim()).orElseGet(() -> {
                        GGConsole.warning("Failed to find trigger of name " + triggerRef.trim() + " when loading creature " + aiCharName);
                        return null;
                    });
                }
                if((version >= 6) && (fileData.getInt() != 0)){
                    byte[] uString = new byte[16];
                    fileData.get(uString);
                    locator1 = mapData.getLocatorByName(new String(uString).trim()).orElseGet(() -> {
                        GGConsole.warning("Failed to find locator of name " + new String(uString).trim() + " when loading creature " + aiCharName);
                        return null;
                    });
                }
                if((version >= 17) && (fileData.getInt() != 0)){
                    byte[] uString = new byte[16];
                    fileData.get(uString);
                    locator2 = mapData.getLocatorByName(new String(uString).trim()).orElseGet(() -> {
                        GGConsole.warning("Failed to find locator of name " + new String(uString).trim() + " when loading creature " + aiCharName);
                        return null;
                    });
                }

                if (version >= 8) {
                    fileData.get();
                    fileData.get();
                    fileData.get();
                    int load2ndTrigger = Byte.toUnsignedInt(fileData.get());
                    fileData.getFloat();
                    fileData.getFloat();

                    if (version >= 10) {
                        fileData.getFloat();
                    }
                    if(load2ndTrigger == 1){
                        String trigger2Ref = Util.getStringFromBuffer(fileData,16);
                        trigger2 = mapData.getTriggerByName(trigger2Ref.trim()).orElseGet(() -> {
                            GGConsole.warning("Failed to find trigger of name " + trigger2Ref.trim() + " when loading creature " + aiCharName);
                            return null;
                        });
                    }
                    if (version >= 11) {
                        fileData.getFloat();
                        fileData.getFloat();
                        fileData.getFloat();
                        fileData.getFloat();
                        fileData.getInt();
                    }
                }




                mapData.ai().creatureSpawns().add(new CreatureSpawn(aiCharName, scriptName, charType, startPoint, startAngle, new String(text), locator1, locator2, trigger1, trigger2, fileAddress, fileData.position() - fileAddress));
            }
            mapData.ai().creatureEndAddress().set(fileData.position());

            //System.out.println("Done AI");
            // System.out.println("Guard:" + fileData.position());
            //489
            /*if(0xc < version){
                int object6Count = fileData.getInt();
                for (int i = 0; i < object6Count; i++) {
                    var f1 = fileData.getFloat();
                    var f2 = fileData.getFloat();
                    var f3 = fileData.getFloat();
                    fileData.getFloat();

                    fileData.getFloat();
                    fileData.getFloat();
                    if(version < 0xf){
                        fileData.get();
                        fileData.get();
                        fileData.get();
                    }else{
                        fileData.getInt();
                        fileData.getFloat();
                        fileData.getFloat();
                        fileData.get();
                        fileData.get();
                        fileData.get();
                    }
                    fileData.get();
                    int flaggy = Byte.toUnsignedInt(fileData.get());
                    if(flaggy != 0){
                        Util.getStringFromBuffer(fileData,flaggy);
                        //specialobject connected special
                        Vector3f pos = new Vector3f(-fileData.getFloat(),fileData.getFloat(),fileData.getFloat());
                        //MapViewer.textPoints.add(Tuple.of(new String(name),pos));
                        if(0xe < version){
                            fileData.getInt();
                        }
                    }
                }
            }*/
        }
    }
}
