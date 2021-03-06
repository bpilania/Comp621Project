// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * Describes the position and nature of a problem encountered while compiling
 * source. This can include matlab to natlab translation problems, parsing 
 * problems, and general compilation problems
 */

public class CompilationProblem {
  private final boolean located;
  private final int line;
  private final int col;
  private final String msg;

  public CompilationProblem(int line, int col, String msg) {
    this.line = line;
    this.col = col;
    this.msg = msg;
    this.located = true;
  }
  
  public CompilationProblem(String msg) {
    this.line = -1;
    this.col = -1;
    this.msg = msg;
    this.located = false;
  }

  public CompilationProblem(String format, Object... args) {
    this(String.format(format, args));
  }

  public boolean hasLocation() {
    return located;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return col;
  }

  public String getMessage() {
    return msg;
  }

  public String toString() {
    if (located) {
      return String.format("[%d, %d] %s", line, col, msg);
    }
    return msg;
  }

  public static String toStringAll(List<CompilationProblem> errors) {
    return Joiner.on('\n').join(errors);
  }
}