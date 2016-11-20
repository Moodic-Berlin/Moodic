#!/usr/bin/env bash

FB_PAGE_ID=$1

curl -X DELETE -H "Content-Type: application/json" -d '{
  "setting_type":"call_to_actions",
  "thread_state":"new_thread"
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token=$FB_PAGE_ID"

curl -X POST -H "Content-Type: application/json" -d '{
  "setting_type":"call_to_actions",
  "thread_state":"new_thread",
  "call_to_actions":[
    {
      "payload":"THE_VERY_FIRST_BUTTON"
    }
  ]
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token=$FB_PAGE_ID"


curl -X DELETE -H "Content-Type: application/json" -d '{
  "setting_type":"greeting"
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token=$FB_PAGE_ID"


curl -X POST -H "Content-Type: application/json" -d '{
  "setting_type":"greeting",
  "greeting":{
    "text": "Hi, this is Moodic bot! Chat with me and I will find you some good music!"
  }
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token=$FB_PAGE_ID"

