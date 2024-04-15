#
# Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

#!/bin/bash
startCse(){
  if [ -f ${ROOT_PATH}/Local-CSE-2.1.3-linux-amd64.zip ];then
    echo "==========start cse============"
    unzip ${ROOT_PATH}/Local-CSE-2.1.3-linux-amd64.zip -d ${ROOT_PATH}/cse
    sh ${ROOT_PATH}/cse/start.sh &
  else
    echo "==========can not find cse software============"
    exit 2
  fi
}
startCse
