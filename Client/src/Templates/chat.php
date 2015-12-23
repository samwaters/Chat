<?php
/**
 * Created by Sam.
 * At: 19/11/2015 02:49
 */
include_once("partials/header_open.php");
?>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css" />
  <link rel="stylesheet" type="text/css" href="resources/css/chat.css" />
  <title>Chat 0.1</title>
<?php include_once("partials/header_close.php"); ?>
  <nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
      <ul class="nav navbar-nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="glyphicon glyphicon-adjust"></span> Themes <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="#">Default Theme</a></li>
            <li><a href="#">Dark Theme</a></li>
          </ul>
        </li>
      </ul>
      <ul class="nav navbar-nav pull-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="glyphicon glyphicon-exclamation-sign"></span> Notifications <span class="badge">2</span></span><span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="#">Friend request from User 4</a></li>
            <li><a href="#">Invitation to join Room3 from User 4</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">Mark All As Read</a></li>
          </ul>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="glyphicon glyphicon-user"></span> Sam <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="#">Profile</a></li>
            <li><a href="#">Settings</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">Log Out</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>
  <div id="application" class="container-fluid">
    <div class="row h100">
      <div class="col-md-10 col-sm-12 tabbable col-narrow h100">
        <div class="tab-content">
          <div id="dashboard" class="tab-pane active">
            <div class="row">
              <div class="col-md-6 col-sm-12 tile">
                <h3 data-toggle="collapse" data-target="#friends-list" aria-expanded="true">Friends <input type="text" id="filter-friends" placeholder="Filter" /></h3>
                <div id="friends-list" class="tile-content collapse in" aria-expanded="true">
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Call</a></li>
                        <li><a href="#">Direct Message</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-star"></span> Parzival
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Call</a></li>
                        <li><a href="#">Direct Message</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-star"></span> Aech
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Call</a></li>
                        <li><a href="#">Direct Message</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-star"></span> Art3mis
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Call</a></li>
                        <li><a href="#">Direct Message</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-star"></span> Daito
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Call</a></li>
                        <li><a href="#">Direct Message</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-star"></span> Shoto
                  </div>
                </div>
              </div>
              <div class="col-md-6 col-sm-12 tile">
                <h3 data-toggle="collapse" data-target="#favourite-rooms-list" aria-expanded="true">Favourite Rooms <input type="text" id="filter-rooms" placeholder="Filter" /></h3>
                <div id="favourite-rooms-list" class="tile-content collapse in" aria-expanded="true">
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Join</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-home"></span> The Hatchery
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Join</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-home"></span> High 5
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Join</a></li>
                        <li><a href="#">Remove</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-home"></span> Oology
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 col-sm-12 tile">
                <h3 data-toggle="collapse" data-target="#users-list" aria-expanded="true">Users <input type="text" id="filter-users" placeholder="Filter" /></h3>
                <div id="users-list" class="tile-content collapse in" aria-expanded="true">
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Friend</a></li>
                        <li><a href="#">Block</a></li>
                        <li><a href="#">Direct Message</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-user"></span> User 1
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Friend</a></li>
                        <li><a href="#">Block</a></li>
                        <li><a href="#">Direct Message</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-user"></span> User 2
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Friend</a></li>
                        <li><a href="#">Block</a></li>
                        <li><a href="#">Direct Message</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-user"></span> User 3
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Friend</a></li>
                        <li><a href="#">Block</a></li>
                        <li><a href="#">Direct Message</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-user"></span> User 4
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Unblock</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-ban-circle"></span> User 5
                  </div>
                </div>
              </div>
              <div class="col-md-6 col-sm-12 tile">
                <h3 data-toggle="collapse" data-target="#room-list" aria-expanded="true">Rooms <input type="text" id="filter-rooms" placeholder="Filter" /></h3>
                <div id="room-list" class="tile-content collapse in" aria-expanded="true">
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Favourite</a></li>
                        <li><a href="#">Join</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-comment"></span> Gunters
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Favourite</a></li>
                        <li><a href="#">Join</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-comment"></span> High 5
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Favourite</a></li>
                        <li><a href="#">Join</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-comment"></span> IOI
                  </div>
                  <div class="tile-list-item">
                    <div class="tile-list-options">
                      <ul>
                        <li><a href="#">Add Favourite</a></li>
                        <li><a href="#">Join</a></li>
                      </ul>
                    </div>
                    <span class="glyphicon glyphicon-comment"></span> Oology
                  </div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> The Hatchery</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Gunters</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> High 5</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> IOI</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Oology</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> The Hatchery</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Gunters</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> High 5</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> IOI</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Oology</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> The Hatchery</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Gunters</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> High 5</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> IOI</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> Oology</div>
                  <div class="tile-list-item"><span class="glyphicon glyphicon-comment"></span> The Hatchery</div>
                </div>
              </div>
            </div>
          </div>
          <div id="chat-public" class="tab-pane">
            <div class="room-info">Welcome to <strong>Public Chat</strong>! General chat and topics that don't belong anywhere else</div>
            <div class="message">
              <span class="timestamp">06:19</span>
              <span class="fromuser">[Parzival]</span>
              <span class="messagetext">Hey People</span>
            </div>
            <div class="message">
              <span class="timestamp">06:20</span>
              <span class="fromuser">[Parzival]</span>
              <span class="messagetext">What's going on</span>
            </div>
            <div class="message">
              <span class="timestamp">06:20</span>
              <span class="fromuser">[Aech]</span>
              <span class="messagetext">Yo</span>
            </div>
          </div>
          <div id="chat-room1" class="tab-pane">
            Room 1
          </div>
          <div id="chat-room2" class="tab-pane">
            Room 2
          </div>
        </div>
      </div>
      <div class="col-md-2 col-sm-12 col-narrow">
        <div id="room-select">
          <ul class="nav nav-pills nav-stacked room-select">
            <li class="active"><a href="#dashboard" data-toggle="tab">Dashboard</a></li>
            <li><a href="#chat-public" data-toggle="tab">Public <span class="badge">4</span></a></li>
            <li><a href="#chat-room1" data-toggle="tab">Room 1</a></li>
            <li><a href="#chat-room2" data-toggle="tab">Room 2 <span class="badge">12</span></a></li>
          </ul>
        </div>
        <div id="current-users">
          <ul>
            <li class="user">
              <span class="status active"></span> User 1
              <ul class="user-options">
                <li><a href="#">Add</a></li>
                <li><a href="#">Block</a></li>
                <li><a href="#">Profile</a></li>
              </ul>
            </li>
            <li class="user">
              <span class="status active"></span> User 2
              <ul class="user-options">
                <li><a href="#">Add</a></li>
                <li><a href="#">Block</a></li>
                <li><a href="#">Profile</a></li>
              </ul>
            </li>
            <li class="user">
              <span class="status active"></span> User 3
              <ul class="user-options">
                <li><a href="#">Add</a></li>
                <li><a href="#">Block</a></li>
                <li><a href="#">Profile</a></li>
              </ul>
            </li>
            <li class="user">
              <span class="status active"></span> User 4
              <ul class="user-options">
                <li><a href="#">Add</a></li>
                <li><a href="#">Block</a></li>
                <li><a href="#">Profile</a></li>
              </ul>
            </li>
            <li class="user">
              <span class="status active"></span> User 5
              <ul class="user-options">
                <li><a href="#">Add</a></li>
                <li><a href="#">Block</a></li>
                <li><a href="#">Profile</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div id="controls">
      <div class="options">
        <button class="btn btn-default btn-xs" data-toggle="modal" data-target="#mdlEmoticons"><span class="glyphicon glyphicon-heart-empty"></span></button>
        <button class="btn btn-default btn-xs" data-toggle="modal" data-target="#mdlFontSettings"><span class="glyphicon glyphicon-font"></span></button>
        <button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-earphone"></span></button>
        <button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-volume-off"></span></button>
      </div>
      <input type="text" id="message" />
    </div>
  </div>
  <div id="modals">
    <div class="modal fade" id="mdlConnecting" role="dialog" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4><span class="glyphicon glyphicon-globe"></span> Connecting</h4>
          </div>
          <div class="modal-body">
            <img src="resources/img/loading.gif" /> Connecting to the server, please wait
          </div>
        </div>
      </div>
    <div class="modal fade" id="mdlEmoticons" role="dialog">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4><span class="glyphicon glyphicon-heart-empty"></span> Emoticons</h4>
          </div>
          <div class="modal-body">
            Potato
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="mdlFontSettings" role="dialog">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4><span class="glyphicon glyphicon-font"></span> Font Settings</h4>
          </div>
          <div class="modal-body">
            Potato
          </div>
          <div class="modal-footer">
            <div class="pull-left">
              <button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
            </div>
            <button type="button" class="btn btn-success" data-dismiss="modal">Save Changes</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script type="text/javascript" src="resources/js/managers/app.js"></script>
  <script type="text/javascript" src="resources/js/managers/chat.js"></script>
  <script type="text/javascript" src="resources/js/managers/chatui.js"></script>
  <script type="text/javascript" src="resources/js/events.js"></script>
  <script type="text/javascript" src="resources/js/main.js"></script>
  <script type="text/javascript">
    $("#mdlConnecting").modal();
  </script>

<?php
include_once("partials/footer.php");
