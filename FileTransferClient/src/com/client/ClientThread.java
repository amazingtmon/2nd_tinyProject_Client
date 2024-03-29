package com.client;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import vo.client.Protocol;


public class ClientThread extends Thread{
	ServerConnecter connect = null;
	public ClientThread(ServerConnecter sercon) {
		this.connect = sercon;
	}

	@Override
	public void run() {
		boolean isStop = false;
		while(!isStop) {
			try {
				String msg = connect.ois.readObject().toString();
				System.out.println("클라이언트값"+msg);
				StringTokenizer st = new StringTokenizer(msg, "#");
				switch(st.nextToken()) {
				case Protocol.checkLogin:{//100번
					LoginModel log = new LoginModel(connect);
					log.resultLogin(st.nextToken());

				}break;
				case Protocol.addUser:{//110번
					LoginModel join = new LoginModel();
					join.addResult(st.nextToken());
				}
				case Protocol.showUser:{//120번
					String p_onlineUser = st.nextToken();//온라인유저
					String p_offlineUser = st.nextToken();//오프라인유저

					MainModel main = new MainModel();
					main.filter(p_onlineUser, p_offlineUser);

				}break;
				case Protocol.createRoom1:{//200번#p_id#roomName
					String  p_id= st.nextToken();
					String roomName = st.nextToken();
					ChattingForm chat = new ChattingForm(connect, Protocol.p_id, roomName);
					connect.chatRoom.put(roomName, chat);
					System.out.println(connect.chatRoom.keySet());
					// 이제 초대한 사람들한테도 채팅룸 뿌려야함.
					//아니지 채팅룸을 뿌릴 필요는 없지 폼은 아니지
					//아 이거 메세지에서 해결해야할듯 아래 300번으로 가자
					// 벡터 withRoomIDs 여기에 초대된 아이디들 있음
					//System.out.println("createRoom1 cf 주소번지: "+cf);
				}break;
				case Protocol.createRoomN:{//210번

				}break;
				case Protocol.closeRoom:{//220번

				}break;
				case Protocol.sendMessage:{//300번#p_id(메세지 보낸사람)#roomName#msg
					System.out.println("클라이언트 스레드 Protocol.sendMessage 실행");
					String p_id = st.nextToken();
					String roomName = st.nextToken();
					String chat_msg = st.nextToken(); //msg
					
					boolean success = true;
					for(String room:connect.chatRoom.keySet()) {
						System.out.println(room+"+"+roomName);
						if(room.equals(roomName)) {
							ChattingForm chat = null;
							chat = connect.chatRoom.get(roomName); //주소번지 들어감
							chat.jta_display.append(p_id+" : "+chat_msg+"\n");
							success = false;
						}
					}
					if(success) {
						ChattingForm chat = new ChattingForm(connect, Protocol.p_id, roomName);
						connect.chatRoom.put(roomName, chat);
						chat.jta_display.append(p_id+" : "+chat_msg+"\n");
					}
					
				}break;
				case Protocol.sendEmoticon:{//310번

				}break;
				case Protocol.sendFile:{//320번

				}break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
}
