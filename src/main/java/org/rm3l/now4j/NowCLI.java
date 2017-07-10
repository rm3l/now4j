/*
 * Copyright (c) 2017 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.rm3l.now4j;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.rm3l.now4j.cli.Args;

public class NowCLI {

    public static void main(String... argv) throws ReflectiveOperationException {

        final Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        final NowClient nowClient;
        final String tokenFromCLI = args.getTokenFromCLI();
        if (tokenFromCLI == null || tokenFromCLI.isEmpty()) {
            //Default Now Client, with no option => read from  /.now.json file
            nowClient = NowClient.create();
        } else {
            //There is a token option
            nowClient = NowClient.create(tokenFromCLI, args.getTeam());
        }

        final String command = args.getCommand();
        //TODO Implement Case by case, not by reflection
        System.out.println(new Gson().toJson(NowClient.class.getMethod(command).invoke(nowClient)));
    }
}
